package trinsdar.ic2c_json_crops

import com.google.gson.JsonObject
import ic2.api.crops.ICropRegistry
import ic2.core.block.crops.CropRegistry
import ic2.core.block.crops.soils.BaseFarmland
import ic2.core.block.crops.soils.BaseSubSoil
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.ForgeRegistries
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.DIST
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(IC2CJsonCrops.ID)
object IC2CJsonCrops {
    const val ID = "ic2c_json_crops"

    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        MOD_BUS.register(this)
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onCropRegister(event: FMLCommonSetupEvent) {
        if (DIST.isClient){
            writeExampleConfig("crops", "example-crop.json5")
            writeExampleConfig("farmland", "example-farmland.json5")
            writeExampleConfig("seeds", "example-seed.json5")
            writeExampleConfig("subsoils", "example-subsoil.json5")
        }
        readFromFile("crops"){ j ->
            val cropData = cropFromJsonObject(j)
            val id = cropData.id
            var crop = CropRegistry.REGISTRY.getCrop(id)
            if (crop != null){
                throw IllegalArgumentException("Crop ${crop.id()} already exists")
            }
            crop = JsonCrop(cropData)
            CropRegistry.REGISTRY.registerCrop(crop)
        }
        readFromFile("seeds") { j ->
            if (!j.has("item")) throw IllegalArgumentException("seed json missing item element")
            val item = CraftingHelper.getItem(j.get("item").asString, false)
            val seed = seedFromJsonObject(j)
            CropRegistry.REGISTRY.registerBaseSeed(item, seed)
        }
        val function = {j : JsonObject, isSoil: Boolean ->
            val file = if (isSoil) "subsoil" else "farmland"
            if (!j.has("humidity")) throw IllegalArgumentException("$file json missing humidity element")
            if (!j.has("nutrients")) throw IllegalArgumentException("$file json missing nutrients element")
            if (!j.has("blocks")) throw IllegalArgumentException("$file json missing block element")
            val array = j.getAsJsonArray("blocks")
            if (array.isEmpty) throw IllegalArgumentException("blocks array is empty")
            val blocks = ArrayList<Block?>()
            for (element in array) {
                val block = ForgeRegistries.BLOCKS.getValue(ResourceLocation(element.asString))
                if (block == Blocks.AIR) throw IllegalArgumentException("$file json defined an invalid block or air in blocks")
                blocks.add(block)
            }
            if (isSoil){
                val soil = BaseSubSoil(j.get("humidity").asInt, j.get("nutrients").asInt)
                CropRegistry.REGISTRY.registerSubSoil(soil, *blocks.toTypedArray())
            } else {
                val farmland = JsonFarmland(j.get("humidity").asInt, j.get("nutrients").asInt, j.has("canHydrate") && j.get("canHydrate").asBoolean)
                CropRegistry.REGISTRY.registerFarmland(farmland, *blocks.toTypedArray())
            }
        }
        readFromFile("farmlands"){ function.invoke(it, false) }
        readFromFile("subsoils"){ function.invoke(it, true) }
    }
}


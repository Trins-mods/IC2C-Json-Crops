package trinsdar.ic2c_json_crops

import com.google.gson.JsonObject
import ic2.api.crops.CropProperties
import ic2.api.crops.ICrop
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraftforge.common.crafting.CraftingHelper

@JvmRecord
data class JsonCropData(val id: ResourceLocation, val name: String,
                        val discoveredBy: String, val displayItem: ItemStack,
                        val properties: CropProperties, val attributes: List<String>,
                        val textures: List<String>, val growthSteps: Int,
                        val drops: List<ItemStack>, val cropType: ICrop.CropType,
                        val optimalHarvestStep: Int, val growthDuration: List<JsonCropRequirements>,
                        val droppingSeeds: Boolean, val seedDrops: List<ItemStack>
    ){
}

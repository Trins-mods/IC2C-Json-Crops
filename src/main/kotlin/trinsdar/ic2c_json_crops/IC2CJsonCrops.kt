package trinsdar.ic2c_json_crops

import com.google.gson.JsonParser
import net.minecraft.client.Minecraft
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.loading.FMLPaths
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path

@Mod(IC2CJsonCrops.ID)
object IC2CJsonCrops {
    const val ID = "ic2c_json_crops"

    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        MOD_BUS.register(this)
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onCommonSetup(event: FMLCommonSetupEvent){
        val cropJsons = File(FMLPaths.CONFIGDIR.get().toFile(), "ic2c/crops")
        val files = cropJsons.listFiles()
        if (files == null) return
        if (cropJsons.isDirectory) {
            for (cropJson in files) {
                if (cropJson.isFile){
                    if (cropJson.absolutePath.endsWith(".json")){
                        try {
                            val reader = Files.newBufferedReader(cropJson.toPath())
                            val parsed = JsonParser.parseReader(reader).asJsonObject
                        } catch (e: Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }
}
package trinsdar.ic2c_json_crops

import com.google.gson.JsonParser
import ic2.api.crops.ICropRegistry
import ic2.core.block.crops.CropRegistry
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLPaths
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import java.io.File
import java.nio.file.Files

@Mod(IC2CJsonCrops.ID)
object IC2CJsonCrops {
    const val ID = "ic2c_json_crops"

    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        FORGE_BUS.register(this)
        FORGE_BUS.start()
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onCropRegister(event: ICropRegistry.CropRegisterEvent){
        val cropJsons = File(FMLPaths.CONFIGDIR.get().toFile(), "ic2c/crops")
        val files = cropJsons.listFiles() ?: return
        if (cropJsons.isDirectory) {
            for (cropJson in files) {
                if (cropJson.isFile){
                    if (cropJson.absolutePath.endsWith(".json")){
                        var additionalError: String? = null;
                        try {
                            val reader = Files.newBufferedReader(cropJson.toPath())
                            val parsed = JsonParser.parseReader(reader).asJsonObject
                            try {
                                val cropData = cropFromJsonObject(parsed)
                                val id = cropData.id
                                var crop = CropRegistry.REGISTRY.getCrop(id)
                                if (crop != null){
                                    LOGGER.error("---------------------------------")
                                    LOGGER.error("Crop ${crop.id()} already exists, not adding ${cropJson.name}")
                                    LOGGER.error("---------------------------------")
                                    continue
                                }
                                crop = JsonCrop(cropData)
                                CropRegistry.REGISTRY.registerCrop(crop)
                            } catch (e: Exception) {
                                additionalError = parsed.toString()
                                throw e
                            }
                        } catch (e: Exception){
                            if (additionalError != null) {
                                LOGGER.error(additionalError)
                            }
                            LOGGER.error("Crop Json not Valid!", e)
                        }
                    }
                }
            }
        }
    }
}


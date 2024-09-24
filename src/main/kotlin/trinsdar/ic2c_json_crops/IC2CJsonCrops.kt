package trinsdar.ic2c_json_crops

import net.minecraft.client.Minecraft
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS
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

    }
}
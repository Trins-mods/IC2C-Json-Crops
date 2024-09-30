package trinsdar.ic2c_json_crops

import net.minecraft.world.level.block.Block
import java.util.function.Predicate

@JvmRecord
data class JsonCropRequirements(val growth: Int, val minLightLevel: Int, val maxLightLevel: Int, val minHumidity: Int, val maxHumidity: Int, val blocksBelow: Predicate<Block>?)

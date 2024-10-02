package trinsdar.ic2c_json_crops

import ic2.core.block.crops.soils.BaseFarmland
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class JsonFarmland(humidity: Int, nutrients: Int, private val canHydrate: Boolean) : BaseFarmland(humidity, nutrients) {
    override fun getHumidity(state: BlockState?): Int {
        if (this.canHydrate){
            if (state == null) return 0
            if (!state.hasProperty(BlockStateProperties.MOISTURE) || state.getValue(BlockStateProperties.MOISTURE) as Int == 7) return super.getHumidity(state)
            return 0
        }
        return super.getHumidity(state)
    }
}
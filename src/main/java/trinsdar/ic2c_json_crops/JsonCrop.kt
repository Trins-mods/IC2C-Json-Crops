package trinsdar.ic2c_json_crops

import ic2.api.crops.ICropModifier
import ic2.api.crops.ICropTile
import ic2.api.crops.ISeedCrop
import ic2.core.IC2
import ic2.core.block.crops.crops.BaseCrop
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

class JsonCrop(private val data: JsonCropData) : BaseCrop(data.id, data.properties, *data.attributes.toTypedArray()), ISeedCrop {
    override fun getName(): Component {
       return this.translate(data.name)
    }

    override fun discoveredBy(): Component {
        return Component.literal(data.discoveredBy)
    }

    override fun getDisplayItem(): ItemStack {
        return data.displayItem
    }

    @OnlyIn(Dist.CLIENT)
    override fun getTextures(): MutableList<ResourceLocation> {
        val list = ArrayList<ResourceLocation>()
        for (texture in data.textures) {
            list.add(ResourceLocation(texture))
        }
        return list
    }

    @OnlyIn(Dist.CLIENT)
    override fun getTexture(stage: Int): TextureAtlasSprite? {
        return null
    }

    override fun getGrowthSteps(): Int {
        return data.growthSteps
    }

    override fun getDrops(p0: ICropTile?): Array<ItemStack> {
        return data.drops.toTypedArray()
    }

    override fun getCropType(): CropType {
       return data.cropType
    }

    override fun getOptimalHarvestStep(cropTile: ICropTile?): Int {
       return data.optimalHarvestStep
    }

    override fun getGrowthDuration(cropTile: ICropTile): Int {
        val stage = cropTile.growthStage
        return getGrowthRequirement(stage).growth
    }

    override fun canGrow(cropTile: ICropTile): Boolean {
        var grow = super.canGrow(cropTile)
        val stage = cropTile.growthStage
        val growthRequirement = getGrowthRequirement(stage)
        if (growthRequirement.minLightLevel > 0){
            grow = grow && cropTile.lightLevel >= growthRequirement.minLightLevel
        }
        if (growthRequirement.maxLightLevel < 15){
            grow = grow && cropTile.lightLevel <= growthRequirement.maxLightLevel
        }
        if (growthRequirement.minHumidity > 0){
            grow = grow && cropTile.humidity >= growthRequirement.minHumidity
        }
        if (growthRequirement.maxHumidity > 0){
            grow = grow && cropTile.humidity <= growthRequirement.maxHumidity
        }
        if (growthRequirement.blocksBelow != null){
            var foundBLock = false
            for (block in cropTile.blocksBelow){
                if (growthRequirement.blocksBelow.test(block)){
                    foundBLock = true
                }
            }
            grow = grow && foundBLock
        }
        return grow
    }

    fun getGrowthRequirement(stage : Int) : JsonCropRequirements {
        val offset = stage - 1
        if (data.stages.size <= offset) return data.stages.last()
        return data.stages[offset]
    }

    override fun onRightClick(cropTile: ICropTile, player: Player?, hand: InteractionHand?): Boolean {
        val stack = player!!.getItemInHand(hand)
        if (ICropModifier.canToggleSeedMode(stack) && data.droppingSeeds) {
            val data = cropTile.customData
            val newSeed = !data.getBoolean("seed")
            data.putBoolean("seed", newSeed)
            if (IC2.PLATFORM.isSimulating) {
                player.displayClientMessage(
                    this.translate(if (newSeed) "info.crop.ic2.seed_mode.enable" else "info.crop.ic2.seed_mode.disable"),
                    false
                )
            }

            return true
        }
        return super.onRightClick(cropTile, player, hand)
    }

    override fun isDroppingSeeds(p0: ICropTile): Boolean {
       return data.droppingSeeds && p0.customData.getBoolean("seed")
    }

    override fun getSeedDrops(p0: ICropTile?): Array<ItemStack> {
       return data.seedDrops.toTypedArray()
    }
}
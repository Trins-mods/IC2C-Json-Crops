package trinsdar.ic2c_json_crops

import ic2.api.crops.CropProperties
import ic2.api.crops.ICrop
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

@JvmRecord
data class JsonCropData(val id: ResourceLocation, val name: String,
                        val discoveredBy: String, val displayItem: ItemStack,
                        val properties: CropProperties, val attributes: List<String>,
                        val textures: List<String>, val growthSteps: Int,
                        val drops: List<ItemStack>, val cropType: ICrop.CropType,
                        val optimalHarvestStep: Int, val stages: List<JsonCropRequirements>,
                        val droppingSeeds: Boolean, val seedDrops: List<ItemStack>
    ){
}

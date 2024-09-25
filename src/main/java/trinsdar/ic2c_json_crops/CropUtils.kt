@file:JvmName("CropUtils")
package trinsdar.ic2c_json_crops

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import ic2.api.crops.BaseSeed
import ic2.api.crops.CropProperties
import ic2.api.crops.ICrop
import ic2.core.block.crops.CropRegistry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.fml.loading.FMLPaths
import trinsdar.ic2c_json_crops.IC2CJsonCrops.LOGGER
import java.io.File
import java.nio.file.Files
import java.util.function.Consumer
import java.util.function.Function

fun cropFromJsonObject(jsonObject: JsonObject) : JsonCropData {

    if (!jsonObject.has("id")){
        throw IllegalArgumentException("Missing id element")
    }
    if (!jsonObject.has("name")){
        throw IllegalArgumentException("Missing name element")
    }
    if (!jsonObject.has("discoveredBy")){
        throw IllegalArgumentException("Missing discoveredBy element")
    }
    if (!jsonObject.has("displayItem")){
        throw IllegalArgumentException("Missing displayItem element")
    }
    if (!jsonObject.has("properties")){
        throw IllegalArgumentException("Missing properties element")
    }
    val propertiesObject = jsonObject.getAsJsonObject("properties")
    if (!propertiesObject.has("tier") || !propertiesObject.has("chemistry")
        || !propertiesObject.has("consumable") || !propertiesObject.has("defensive")
        || !propertiesObject.has("colorful") || !propertiesObject.has("weed")){
        throw IllegalArgumentException("properties element missing required properties")
    }
    if (!jsonObject.has("attributes")){
        throw IllegalArgumentException("Missing attributes element")
    }
    if (!jsonObject.has("textures")){
        throw IllegalArgumentException("Missing textures element")
    }
    if (!jsonObject.has("growthSteps")){
        throw IllegalArgumentException("Missing growthSteps element")
    }
    if (!jsonObject.has("drops")){
        throw IllegalArgumentException("Missing drops element")
    }
    val id = ResourceLocation(jsonObject.get("id").asString)
    val name = jsonObject.get("name").asString
    val discoveredBy = jsonObject.get("discoveredBy").asString
    val displayItemElement = jsonObject.get("displayItem")
    val displayItem = if (displayItemElement.isJsonObject){
        CraftingHelper.getItemStack(displayItemElement.asJsonObject, true)
    } else {
        ItemStack(CraftingHelper.getItem(displayItemElement.asString, false))
    }
    val properties = CropProperties(propertiesObject.get("tier").asInt, propertiesObject.get("chemistry").asInt,
        propertiesObject.get("consumable").asInt, propertiesObject.get("defensive").asInt,
        propertiesObject.get("colorful").asInt, propertiesObject.get("weed").asInt)
    var array = jsonObject.getAsJsonArray("attributes")
    val attributes = ArrayList<String>()
    for (element in array) {
        attributes.add(element.asString)
    }
    array = jsonObject.getAsJsonArray("textures")
    val textures = ArrayList<String>()
    for (element in array) {
        textures.add(element.asString)
    }

    val growthSteps = jsonObject.get("growthSteps").asInt
    if (textures.size != growthSteps) throw IllegalArgumentException("textures array doesn't have enough elements!")
    array = jsonObject.getAsJsonArray("drops")
    val drops = ArrayList<ItemStack>()
    for (element in array) {
        if (element.isJsonObject){
            drops.add(CraftingHelper.getItemStack(element.asJsonObject, true))
        } else{
            drops.add(ItemStack(CraftingHelper.getItem(element.asString, false)))
        }
    }
    val cropType = if (jsonObject.has("cropType")) {
        ICrop.CropType.valueOf(jsonObject.get("cropType").asString)
    } else ICrop.CropType.AIR
    val optionalHarvestStep = if (jsonObject.has("harvestStep")) jsonObject.get("harvestStep").asInt else growthSteps
    val growthDuration = if (jsonObject.has("growthDuration")) {
        val list = ArrayList<Int>()
        array = jsonObject.getAsJsonArray("growthDuration")
        if (array.isEmpty) list.add(properties.tier * 200)
        for (element in array){
            list.add(element.asInt)
        }
        list
    } else {
        val list = ArrayList<Int>()
        list.add(properties.tier * 200)
        list
    }
    val droppingSeeds = jsonObject.has("droppingSeeds") || jsonObject.getAsJsonObject("droppingSeeds").asBoolean
    val seedDrops = ArrayList<ItemStack>()
    if (jsonObject.has("seedDrops")){
        array = jsonObject.getAsJsonArray("seedDrops")
        for (element in array) {
            if (element.isJsonObject){
                seedDrops.add(CraftingHelper.getItemStack(element.asJsonObject, true))
            } else{
                seedDrops.add(ItemStack(CraftingHelper.getItem(element.asString, false)))
            }
        }
    }
    return JsonCropData(id, name, discoveredBy, displayItem, properties, attributes, textures, growthSteps, drops, cropType, optionalHarvestStep, growthDuration, droppingSeeds, seedDrops)
}

fun seedFromJsonObject(jsonObject: JsonObject): BaseSeed {
    if (!jsonObject.has("crop")) throw IllegalArgumentException("seed json missing crop element")
    val cropString = jsonObject.get("crop").asString
    val crop: ICrop
    if ((CropRegistry.REGISTRY.getCrop(ResourceLocation(cropString)).also { crop = it }) == null)
        throw IllegalArgumentException("crop $cropString does not exist")
    val stage = if (jsonObject.has("stage")) jsonObject.get("stage").asInt else 1
    if (stage > crop.growthSteps) throw IllegalArgumentException("seed defines a growth stage greater then the max")
    val growth = if (jsonObject.has("growth")) jsonObject.get("growth").asInt else 1
    val gain = if (jsonObject.has("gain")) jsonObject.get("gain").asInt else 1
    val resistance = if (jsonObject.has("resistance")) jsonObject.get("resistance").asInt else 1
    val stackSize = if (jsonObject.has("stackSize")) jsonObject.get("stackSize").asInt else 1
    return BaseSeed(crop, stage, growth, gain, resistance, stackSize)
}

fun readFromFile(path: String, function: Consumer<JsonObject>) {
    val cropJsons = File(FMLPaths.CONFIGDIR.get().toFile(), "ic2c/$path")
    val files = cropJsons.listFiles() ?: return
    for (cropJson in files) {
        if (cropJson.isFile){
            if (cropJson.absolutePath.endsWith(".json")){
                var additionalError: String? = null;
                try {
                    val reader = Files.newBufferedReader(cropJson.toPath())
                    val parsed = JsonParser.parseReader(reader).asJsonObject
                    try {
                        function.accept(parsed)
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

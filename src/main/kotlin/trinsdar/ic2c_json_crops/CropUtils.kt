package trinsdar.ic2c_json_crops

import com.google.gson.JsonObject
import ic2.api.crops.BaseSeed
import ic2.api.crops.CropProperties
import ic2.api.crops.ICrop
import ic2.core.block.crops.CropRegistry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraftforge.common.crafting.CraftingHelper

@JvmName("CropUtils")

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


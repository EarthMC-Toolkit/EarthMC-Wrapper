package io.github.emcw.adapters

import com.google.gson.*
import java.awt.Color
import java.lang.reflect.Type

class ColorAdapter : JsonSerializer<Color>, JsonDeserializer<Color> {
    override fun serialize(src: Color, srcType: Type, ctx: JsonSerializationContext): JsonElement {
        val colorString = "#" + Integer.toHexString(src.rgb).substring(2)
        return JsonPrimitive(colorString)
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext): Color {
        return Color.decode(json.asString)
    }
}
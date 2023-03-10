package io.github.emcw.utils;

import java.awt.Color;
import java.lang.reflect.Type;

import com.google.gson.*;

public class ColorAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {
    @Override
    public Color deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        return Color.decode(json.getAsString());
    }

    @Override
    public JsonElement serialize(Color src, Type srcType, JsonSerializationContext ctx) {
        String colorString = "#" + Integer.toHexString(src.getRGB()).substring(2);
        return new JsonPrimitive(colorString);
    }
}
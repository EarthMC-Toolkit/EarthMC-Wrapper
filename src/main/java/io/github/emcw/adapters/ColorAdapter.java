package io.github.emcw.adapters;

import com.google.gson.*;

import java.awt.*;
import java.lang.reflect.Type;

public class ColorAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {
    @Override
    public JsonElement serialize(Color src, Type srcType, JsonSerializationContext ctx) {
        String colorString = "#" + Integer.toHexString(src.getRGB()).substring(2);
        return new JsonPrimitive(colorString);
    }

    @Override
    public Color deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        return Color.decode(json.getAsString());
    }
}
package io.github.emcw.adapters;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Duration;

public class DurationAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
    @Override
    public JsonElement serialize(Duration duration, Type type, JsonSerializationContext ctx) {
        return new JsonPrimitive(duration.toString());
    }

    @Override
    public Duration deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        return Duration.parse(el.getAsString());
    }
}
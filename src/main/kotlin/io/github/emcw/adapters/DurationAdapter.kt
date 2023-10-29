package io.github.emcw.adapters

import com.google.gson.*
import java.lang.reflect.Type
import java.time.Duration

class DurationAdapter : JsonSerializer<Duration>, JsonDeserializer<Duration> {
    override fun serialize(duration: Duration, type: Type, ctx: JsonSerializationContext): JsonElement {
        return JsonPrimitive(duration.toString())
    }

    @Throws(JsonParseException::class)
    override fun deserialize(el: JsonElement, type: Type, ctx: JsonDeserializationContext): Duration {
        return Duration.parse(el.asString)
    }
}
package io.github.emcw.map.entities

import com.google.gson.JsonObject
import io.github.emcw.interfaces.ISerializable
import io.github.emcw.utils.GsonUtil
import lombok.Getter

class Capital(obj: JsonObject?) : ISerializable {
    @Getter val name: String?
    @Getter val location: Location

    init {
        name = GsonUtil.keyAsStr(obj, "name")
        location = Location(GsonUtil.keyAsInt(obj, "x"), GsonUtil.keyAsInt(obj, "z"))
    }
}
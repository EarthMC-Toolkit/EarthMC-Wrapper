package io.github.emcw.map.entities;

import com.google.gson.JsonObject;
import io.github.emcw.interfaces.ISerializable;
import lombok.Getter;

import static io.github.emcw.utils.GsonUtil.keyAsInt;
import static io.github.emcw.utils.GsonUtil.keyAsStr;

public class Capital implements ISerializable {
    @Getter final String name;
    @Getter final Location location;

    public Capital(JsonObject obj) {
        name = keyAsStr(obj, "name");
        location = new Location(keyAsInt(obj, "x"), keyAsInt(obj, "z"));
    }
}
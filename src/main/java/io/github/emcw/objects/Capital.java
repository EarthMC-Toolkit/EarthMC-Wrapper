package io.github.emcw.objects;

import com.google.gson.JsonObject;
import lombok.Getter;

import static io.github.emcw.utils.GsonUtil.keyAsInt;
import static io.github.emcw.utils.GsonUtil.keyAsStr;

public class Capital {
    @Getter final String name;
    @Getter final Location location;

    Capital(JsonObject obj) {
        this.name = keyAsStr(obj, "name");
        this.location = new Location(keyAsInt(obj, "x"), keyAsInt(obj, "z"));
    }
}
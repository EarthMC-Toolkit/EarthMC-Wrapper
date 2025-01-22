package io.github.emcw.squaremap.entities;

import com.google.gson.JsonObject;
import io.github.emcw.interfaces.ISerializable;
import lombok.Getter;

import static io.github.emcw.utils.GsonUtil.keyAsInt;
import static io.github.emcw.utils.GsonUtil.keyAsStr;

public class SquaremapCapital implements ISerializable {
    @Getter final String name;
    @Getter final SquaremapLocation location;

    public SquaremapCapital(JsonObject obj) {
        name = keyAsStr(obj, "name");
        location = new SquaremapLocation(keyAsInt(obj, "x"), keyAsInt(obj, "z"));
    }
}
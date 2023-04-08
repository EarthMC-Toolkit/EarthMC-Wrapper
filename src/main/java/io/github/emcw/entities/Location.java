package io.github.emcw.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.emcw.interfaces.ISerializable;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static io.github.emcw.utils.Funcs.range;
import static io.github.emcw.utils.GsonUtil.*;

public class Location implements ISerializable {
    @Getter Integer x, z, y;

    public Location(Integer x, Integer y, Integer z) {
        this(x, z);
        this.y = y;
    }

    public Location(Integer x, Integer z) {
        this.x = x;
        this.z = z;
    }

    public Location() {
        this(0, 64, 0);
    }

    @Contract("_ -> new")
    public static @NotNull Location fromObj(JsonObject obj) {
        return new Location(
            keyAsInt(obj, "x"),
            keyAsInt(obj, "y"),
            keyAsInt(obj, "z")
        );
    }

    public boolean valid() {
        return x != null && z != null;
    }

    public boolean isDefault() {
        return x == 0 && z == 0;
    }

    public static @NotNull Location of(JsonObject obj) {
        JsonArray xArr = keyAsArr(obj, "x"),
                  zArr = keyAsArr(obj, "z");

        Integer xAverage = range(arrToIntArr(xArr)),
                zAverage = range(arrToIntArr(zArr));

        return new Location(xAverage, zAverage);
    }
}
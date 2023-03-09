package io.github.emcw.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;

import static io.github.emcw.utils.Funcs.range;
import static io.github.emcw.utils.GsonUtil.*;

public class Location {
    @Getter public Integer x, z, y = null;

    Location(Integer x, Integer y, Integer z) {
        this(x, z);
        this.y = y;
    }

    Location(Integer x, Integer z) {
        this.x = x;
        this.z = z;
    }

    Location() {
        this(0, 64, 0);
    }

    static Location fromObj(JsonObject obj) {
        return new Location(
            keyAsInt(obj, "x"),
            keyAsInt(obj, "y"),
            keyAsInt(obj, "z")
        );
    }

    public static Location of(JsonObject obj) {
        JsonArray xArr = keyAsArr(obj, "x"),
                  zArr = keyAsArr(obj, "z");

        Integer xAverage = range(arrToIntArr(xArr)),
                zAverage = range(arrToIntArr(zArr));

        return new Location(xAverage, zAverage);
    }
}
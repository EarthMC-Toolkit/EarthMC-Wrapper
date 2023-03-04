package io.github.emcw.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import static io.github.emcw.utils.GsonUtil.*;

public class Location {
    public final Integer x, y, z;

    Location(Integer x, Integer y, Integer z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    Location(Integer x, Integer z) {
        this(x, 64, z);
    }

    Location() {
        this(0, 64, 0);
    }

    static Location fromObj(JsonObject obj) {
        Integer x = keyAsInt(obj, "x");
        Integer y = keyAsInt(obj, "y");
        Integer z = keyAsInt(obj, "z");

        return new Location(x, y, z);
    }

    public static Location of(JsonArray xArr, JsonArray zArr) {
        Integer xAverage = 0, zAverage = 0;

        return new Location(xAverage, zAverage);
    }
}
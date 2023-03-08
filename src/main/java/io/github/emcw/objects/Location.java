package io.github.emcw.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.Arrays;
import java.util.IntSummaryStatistics;

import static io.github.emcw.utils.GsonUtil.*;

public class Location {
    @Getter
    public Integer x, z, y = null;

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

    public static Integer range(int[] args) {
        IntSummaryStatistics stat = Arrays.stream(args).parallel().summaryStatistics();
        return Math.round((stat.getMin() + stat.getMax()) / 2.0f);
    }
}
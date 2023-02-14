package net.emc.emcw.objects;

import com.google.gson.JsonObject;
import static net.emc.emcw.utils.GsonUtil.keyAsInt;

public class Location {
    public final Integer x, y, z;

//    Location(Integer... coords) {
//        this.x = coords[0];
//        this.z = coords[1];
//        this.y = coords.length > 2 ? coords[2] : 64;
//    }

    Location(Integer x, Integer z, Integer y) {
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
}
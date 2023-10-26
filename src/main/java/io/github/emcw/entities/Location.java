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
    @Getter final Integer x;
    @Getter final Integer z;
    @Getter Integer y;

    /**
     * <p>A location in 3D space.</p>
     * @param x The coordinate on the x-axis (left/right)
     * @param y The coordinate on the y-axis (up/down)
     * @param z The coordinate on the z-axis (forward/backward)
     */
    public Location(Integer x, Integer y, Integer z) {
        this(x, z);
        this.y = y;
    }

    /**
     * <p>A location in 2D space.</p>
     * @param x The coordinate on the x-axis (left/right)
     * @param z The coordinate on the z-axis (up/down)
     */
    public Location(Integer x, Integer z) {
        this.x = x;
        this.z = z;
    }

    /**
     * <p>Default location representing 0, 64, 0.</p>
     */
    public Location() {
        this(0, 64, 0);
    }

    /**
     * Check if this location is properly initialized.
     * <br>Shorthand for <code>x != null && z != null;</code>
     */
    public boolean valid() {
        return x != null && z != null;
    }

    /**
     * Whether this location points to the map center.
     * Shorthand for <code>x == 0 && z == 0;</code>
     */
    public boolean isDefault() {
        return x == 0 && z == 0;
    }

    @Contract("_ -> new")
    protected static @NotNull Location fromObj(JsonObject obj) {
        return new Location(keyAsInt(obj, "x"), keyAsInt(obj, "y"), keyAsInt(obj, "z"));
    }

    protected static @NotNull Location of(JsonObject obj) {
        JsonArray xArr = keyAsArr(obj, "x"),
                  zArr = keyAsArr(obj, "z");

        Integer xAverage = range(arrToIntArr(xArr)),
                zAverage = range(arrToIntArr(zArr));

        return new Location(xAverage, zAverage);
    }
}
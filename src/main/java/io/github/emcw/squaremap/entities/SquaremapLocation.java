package io.github.emcw.squaremap.entities;

import lombok.Getter;

import io.github.emcw.interfaces.IGsonSerializable;
import static io.github.emcw.utils.Funcs.midrange;

@SuppressWarnings("unused")
public class SquaremapLocation implements IGsonSerializable {
    @Getter final Integer x;
    @Getter final Integer y;
    @Getter final Integer z;

    /**
     * A point in 2D space.
     * @param x The coordinate on the x-axis (left/right).
     * @param z The coordinate on the z-axis (up/down).
     */
    public SquaremapLocation(Integer x, Integer z) {
        this.x = x;
        this.y = null;
        this.z = z;
    }

    /**
     * A point in 3D space.
     * @param x The coordinate on the x-axis (left/right).
     * @param y The coordinate on the z-axis (up/down).
     * @param z The coordinate on the z-axis (forward/backward).
     */
    public SquaremapLocation(Integer x, Integer y, Integer z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Check if this location is properly initialized.<br><br>
     * Shorthand for {@code x != null && z != null.}
     */
    public boolean valid() {
        return x != null && z != null;
    }

    /**
     * Whether this location points to the map center.<br><br>
     * Shorthand for {@code x == 0 && z == 0.}
     */
    public boolean isDefault() {
        return x == 0 && z == 0;
    }

    // TODO: Try to use capital X and Z, with midrange as fallback.
    public static SquaremapLocation of(int[] xArr, int[] zArr) {
        Integer x = midrange(xArr);
        Integer z = midrange(zArr);

        return new SquaremapLocation(x, z);
    }
}
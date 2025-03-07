package io.github.emcw.squaremap.entities;

import lombok.Getter;

import io.github.emcw.interfaces.IGsonSerializable;
import static io.github.emcw.utils.Funcs.midrange;

@Getter
@SuppressWarnings("unused")
public class SquaremapLocation implements IGsonSerializable {
    private final Integer x, y, z;

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
     * Shorthand for {@code x != null && z != null}.
     */
    public boolean isValidPoint() {
        return x != null && z != null;
    }

    /**
     * Whether this location points to the center of the map.<br><br>
     * Shorthand for {@code x == 0 && z == 0}.
     */
    public boolean isMapCenter() {
        return x == 0 && z == 0;
    }

    // TODO: Try to use capital X and Z, with midrange as fallback.
    public static SquaremapLocation of(int[] xArr, int[] zArr) {
        int midX = midrange(xArr);
        int midZ = midrange(zArr);

        return new SquaremapLocation(midX, midZ);
    }
}
package io.github.emcw.interfaces;

import io.github.emcw.entities.*;

import java.util.Map;

import static io.github.emcw.utils.Funcs.collectAsMap;
import static io.github.emcw.utils.Funcs.withinRadius;
import static io.github.emcw.utils.GsonUtil.streamEntries;

/**
 * This class specifies that an entity is locatable and provides methods to get other nearby locatable entities.
 * @param <T> The type of entity we are implementing this interface into.
 *            Must be specified so the correct locations are returned when checking nearby.
 */
public interface ILocatable<T> {
    Integer[] INT_ARRAY_X = new Integer[2];
    Integer[] INT_ARRAY_Z = new Integer[2];

    default Map<String, T> getNearby(Map<String, T> map, Integer xCoord, Integer zCoord, Integer radius) {
        return getNearby(map, xCoord, zCoord, radius, radius);
    }

    /**
     * Returns a new map of entities that are within the specified x and z radii given the coordinates.
     * <p></p>
     * <p><b>Important notes: </b></p>
     * <p>Filtering is performed in parallel and is not guaranteed to be thread-safe.
     * In addition, the resulting map will be collected with any nulls removed.
     * @param map The map of entities to be checked against. Usually online players, but accepts a mix of Player, Resident, Town or Nation.
     * @param xCoord The x coordinate.
     * @param zCoord The z coordinate.
     * @param xRadius The radius to check within on the x-axis. (Left/Right)
     * @param zRadius The radius to check within on the z-axis. (Up/Down)
     * @return A new map of entities where each entry key is its name, and the value is itself.
     */
    default Map<String, T> getNearby(Map<String, T> map,
            Integer xCoord, Integer zCoord,
            Integer xRadius, Integer zRadius) {

        INT_ARRAY_X[0] = xCoord;
        INT_ARRAY_X[1] = xRadius;

        INT_ARRAY_Z[0] = zCoord;
        INT_ARRAY_Z[1] = zRadius;

        return collectAsMap(streamEntries(map).filter(entry -> checkNearby(entry.getValue())));
    }

    private boolean checkNearby(T val) {
        Location loc = null;

        if (val instanceof Player player) {
            if (!player.visible()) return false;
            loc = player.getLocation();
        }
        else if (val instanceof Town town) loc = town.getLocation();
        else if (val instanceof Nation nation) loc = nation.getCapital().getLocation();

        return loc != null && !loc.isDefault() && isNearby(loc);
    }

    private boolean isNearby(Location location) {
        if (location == null) return false;

        int x = location.getX(), z = location.getZ();
        return withinRadius(x, INT_ARRAY_X) && withinRadius(z, INT_ARRAY_Z);
    }
}
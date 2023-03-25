package io.github.emcw.interfaces;

import io.github.emcw.entities.*;

import java.util.Map;

import static io.github.emcw.utils.Funcs.collectAsMap;
import static io.github.emcw.utils.Funcs.hypot;
import static io.github.emcw.utils.GsonUtil.streamEntries;

public interface ILocatable<T> {
    Integer[] INT_ARRAY_X = new Integer[2];
    Integer[] INT_ARRAY_Z = new Integer[2];

    default Map<String, T> getNearby(Map<String, T> map, Integer xCoord, Integer zCoord, Integer radius) {
        return getNearby(map, xCoord, zCoord, radius, radius);
    }

    default Map<String, T> getNearby(Map<String, T> map,
            Integer xCoord, Integer zCoord,
            Integer xRadius, Integer zRadius) {

        INT_ARRAY_X[0] = xCoord;
        INT_ARRAY_X[1] = xRadius;

        INT_ARRAY_Z[0] = zCoord;
        INT_ARRAY_Z[1] = zRadius;

        return collectAsMap(streamEntries(map).filter(entry -> checkNearby(entry.getValue(), INT_ARRAY_X, INT_ARRAY_Z)));
    }

    private boolean checkNearby(T val, Integer[] xArr, Integer[] zArr) {
        Location loc = null;

        if (val instanceof Player) loc = ((Player) val).getLocation();
        else if (val instanceof Town) loc = ((Town) val).getLocation();
        else if (val instanceof Nation) loc = ((Nation) val).getCapital().getLocation();

        return loc != null && !loc.isDefault() && isNearby(loc, xArr, zArr);
    }

    default boolean isNearby(Location location, Integer[] xArr, Integer[] zArr) {
        int x = location.getX(), z = location.getZ();
        return hypot(x, xArr) && hypot(z, zArr);
    }
}
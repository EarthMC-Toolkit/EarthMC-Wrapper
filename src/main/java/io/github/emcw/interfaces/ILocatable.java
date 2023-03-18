package io.github.emcw.interfaces;

import io.github.emcw.objects.Location;
import io.github.emcw.objects.Nation;
import io.github.emcw.objects.Player;
import io.github.emcw.objects.Town;

import java.util.Map;
import java.util.function.Predicate;

import static io.github.emcw.utils.Funcs.collectAsMap;
import static io.github.emcw.utils.Funcs.hypot;
import static io.github.emcw.utils.GsonUtil.streamEntries;

public interface ILocatable<T> {
    Integer[] INT_ARRAY = new Integer[2];

    default Map<String, T> getNearby(Map<String, T> map, Integer xCoord, Integer zCoord, Integer radius) {
        return getNearby(map, xCoord, zCoord, radius, radius);
    }

    default Map<String, T> getNearby(Map<String, T> map,
                                     Integer xCoord, Integer zCoord,
                                     Integer xRadius, Integer zRadius) {

        return collectAsMap(streamEntries(map).filter(nearbyPredicate(
                intArr(xCoord, xRadius), intArr(zCoord, zRadius))
        ));
    }

    private Predicate<? super Map.Entry<String, T>> nearbyPredicate(Integer[] xArr, Integer[] zArr) {
        return entry -> {
            Location loc = null;
            T val = entry.getValue();

            if (val instanceof Player) loc = ((Player) val).getLocation();
            else if (val instanceof Town) loc = ((Town) val).getLocation();
            else if (val instanceof Nation) loc = ((Nation) val).getCapital().getLocation();

            return loc != null && isNearby(loc, xArr, zArr);
        };
    }

    private boolean isNearby(Location location, Integer[] xArr, Integer[] zArr) {
        int x = location.getX(), z = location.getZ();
        return (x != 0 && z != 0) && hypot(x, xArr) && hypot(z, zArr);
    }

    private Integer[] intArr(Integer coord, Integer radius) {
        INT_ARRAY[0] = coord;
        INT_ARRAY[1] = radius;
        return INT_ARRAY;
    }
}

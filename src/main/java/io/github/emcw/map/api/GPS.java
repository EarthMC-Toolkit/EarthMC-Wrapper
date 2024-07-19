package io.github.emcw.map.api;

import io.github.emcw.EMCMap;
import io.github.emcw.map.entities.Location;
import io.github.emcw.map.entities.Nation;

import com.github.jafarlihi.eemit.EventEmitter;
import io.github.emcw.map.entities.Town;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static io.github.emcw.utils.Funcs.collectEntities;
import static io.github.emcw.utils.GsonUtil.streamValues;

record Route(Nation nation, Integer distance, String direction) {}
record RouteType(boolean avoidPvp, boolean avoidPublic) {
    public static final RouteType SAFEST = new RouteType(true, true);
    public static final RouteType FASTEST = new RouteType(false, false);
    public static final RouteType AVOID_PUBLIC = new RouteType(false, true);
    public static final RouteType AVOID_PVP = new RouteType(true, false);
}

public class GPS extends EventEmitter<Object> {
    private final EMCMap parent;

    private final Location lastLoc = null;
    private final Location emittedHidden = null;

    public GPS(EMCMap parent) {
        this.parent = parent;
    }

    public GPS track() {
        return this;
    }

    public Route safestRoute(Location loc) {
        return findRoute(loc, RouteType.SAFEST);
    }

    public Route fastestRoute(Location loc) {
        return findRoute(loc, RouteType.FASTEST);
    }

    public Route findRoute(Location loc, RouteType route) {
        if (loc == null || !loc.valid()) {
            new IllegalArgumentException(
                "Cannot find route! Inputted location is invalid:\n" + loc
            ).printStackTrace();
        }

        Map<String, Town> towns = this.parent.Towns.all();
        Map<String, Nation> nations = this.parent.Nations.all();

        Map<String, Nation> filtered = collectEntities(streamValues(nations).filter(nation -> {
            Town capital = nation.getCapital();
            if (towns.containsKey(capital.getName())) return false;

            Town.Flags flags = capital.getFlags();

            boolean PVP = route.avoidPvp() && flags.PVP;
            boolean capitalIsPublic = route.avoidPublic() && !flags.PUBLIC;

            return !PVP && !capitalIsPublic;
        }));

        String direction = cardinalDirection(new Location(), new Location());
        return new Route(null, 0, direction);
    }

    static @NotNull String cardinalDirection(@NotNull Location origin, @NotNull Location destination) {
        int deltaX = origin.getX() - destination.getX();
        int deltaZ = origin.getZ() - destination.getZ();

        double angle = Math.atan2(deltaZ, deltaX) * 180 / Math.PI;

        // Determine the cardinal direction
        if (angle >= -45 && angle < 45)
            return "east";

        if (angle >= 45 && angle < 135)
            return "north";

        if (angle >= 135 || angle < -135)
            return "west";

        return "south";
    }
}
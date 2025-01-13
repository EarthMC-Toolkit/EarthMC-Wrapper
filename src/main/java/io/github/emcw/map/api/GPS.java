package io.github.emcw.map.api;

import io.github.emcw.Direction;
import io.github.emcw.EMCMap;
import io.github.emcw.map.entities.Location;
import io.github.emcw.map.entities.Nation;

import com.github.jafarlihi.eemit.EventEmitter;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

public class GPS extends EventEmitter<Object> {
    private final Location lastLoc = null;
    private final Location emittedHidden = null;

    Towns towns;
    Nations nations;

    public record Route(Nation nation, Integer distance, Direction direction) {}
    public record RouteType(boolean avoidPvp, boolean avoidPublic) {
        public static final RouteType SAFEST = new RouteType(true, true);
        public static final RouteType FASTEST = new RouteType(false, false);
        public static final RouteType AVOID_PUBLIC = new RouteType(false, true);
        public static final RouteType AVOID_PVP = new RouteType(true, false);
    }

    public GPS(@NotNull EMCMap mapInstance) {
        towns = mapInstance.Towns;
        nations = mapInstance.Nations;
    }

//    public GPS track() {
//        return this;
//    }

    public Route safestRoute(Location loc) {
        return findRoute(loc, RouteType.SAFEST);
    }

    public Route fastestRoute(Location loc) {
        return findRoute(loc, RouteType.FASTEST);
    }

    public Route findRoute(Location loc, RouteType route) {
        throw new NotImplementedException("Route finding is not implemented yet!");

//        if (loc == null || !loc.valid()) {
//            new IllegalArgumentException("Cannot find route! Inputted location is invalid:\n" + loc)
//                .printStackTrace();
//        }
//
//        Map<String, Town> towns = this.towns.all();
//        Map<String, Nation> nations = this.nations.all();
//
//        Map<String, Nation> filtered = collectEntities(streamValues(nations).filter(nation -> {
//            Town capital = nation.getCapital();
//            if (towns.containsKey(capital.getName())) return false;
//
//            Town.Flags flags = capital.getFlags();
//
//            boolean PVP = route.avoidPvp() && flags.PVP;
//            boolean capitalIsPublic = route.avoidPublic() && !flags.PUBLIC;
//
//            return !PVP && !capitalIsPublic;
//        }));
//
//        Direction direction = cardinalDirection(new Location(0, 0), new Location(0, 0));
//        return new Route(null, 0, direction);
    }

    static @NotNull Direction cardinalDirection(@NotNull Location origin, @NotNull Location destination) {
        int deltaX = origin.getX() - destination.getX();
        int deltaZ = origin.getZ() - destination.getZ();

        double angle = Math.atan2(deltaZ, deltaX) * 180 / Math.PI;

        // Determine the cardinal direction
        if (angle >= -45 && angle < 45)
            return Direction.EAST;

        if (angle >= 45 && angle < 135)
            return Direction.NORTH;

        if (angle >= 135 || angle < -135)
            return Direction.WEST;

        return Direction.SOUTH;
    }
}
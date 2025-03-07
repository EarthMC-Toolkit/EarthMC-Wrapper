package io.github.emcw.squaremap.api;

import io.github.emcw.KnownMap;
import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;
import io.github.emcw.common.Point2D;
import io.github.emcw.squaremap.entities.SquaremapLocation;
import io.github.emcw.squaremap.entities.SquaremapNation;
import io.github.emcw.squaremap.entities.SquaremapTown;

import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.squaremap.SquaremapParser;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.emcw.utils.Funcs.euclidean;
import static io.github.emcw.utils.GsonUtil.streamValues;

@SuppressWarnings("unused")
public class Towns extends BaseCache<SquaremapTown> implements ILocatable<SquaremapTown> {
    private final KnownMap map;
    private final SquaremapParser parser;

    public Towns(KnownMap map, SquaremapParser parser, CacheOptions options) {
        super(options);

        this.map = map;
        this.parser = parser;
    }

    @Override
    protected Map<String, SquaremapTown> fetchCacheData() {
        try {
            this.parser.parseMapData(true, false, true);
        } catch (Exception e) {
            System.err.println("[EMCW - Towns] Error fetching cache data:\n  " + e.getMessage());
            return null;
        }

        return this.parser.getTowns();
    }

//    public Map<String, SquaremapTown> getWithinDistance(Point2D loc, int range) {
//        Stream<SquaremapTown> towns = streamValues(getAll()).map(town -> {
//            int dist = town.distanceFrom(loc);
//            return dist < range ? town : null;
//        }).filter(Objects::nonNull);
//
//        return towns.collect(Collectors.toMap(SquaremapTown::getName, Function.identity()));
//    }

    /**
     * Gets all towns that the specified nation can invite by measuring their distances.
     * Includes all towns within 3500 blocks on Aurora or 3000 blocks on other maps.
     * @param nation The nation we want to gather all invitable towns for.
     * @return A new {@link Map} containing the in-range (invitable) towns where the key is the town name.
     */
    public Map<String, SquaremapTown> invitableFromNation(SquaremapNation nation) {
        SquaremapLocation capitalLoc = nation.getCapital().getLocation();
        if (!capitalLoc.isValidPoint()) {
            return null;
        }

        Point2D capitalPoint = new Point2D(capitalLoc.getX(), capitalLoc.getZ());

        Stream<SquaremapTown> towns = streamValues(getAll()).map(town -> {
            if (town.getNation() != null) {
                return null; // Town already has a nation.
            }

            SquaremapLocation townLoc = town.getLocation();
            if (!townLoc.isValidPoint()) {
                return null;
            }

            Point2D townPoint = new Point2D(townLoc.getX(), townLoc.getZ());

            // TODO: Confirm invite is determined by range from capital and not home block.
            double distToTown = euclidean(capitalPoint, townPoint);
            int inviteRange = this.map == KnownMap.AURORA ? 3500 : 3000;

            // Return town if in range.
            return distToTown < inviteRange ? town : null;
        }).filter(Objects::nonNull);

        return towns.collect(Collectors.toMap(SquaremapTown::getName, Function.identity()));
    }
}
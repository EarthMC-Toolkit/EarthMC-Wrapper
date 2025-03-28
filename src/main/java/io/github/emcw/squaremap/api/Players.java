package io.github.emcw.squaremap.api;

import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;

import io.github.emcw.squaremap.SquaremapParser;
import io.github.emcw.squaremap.entities.SquaremapLocation;

import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.squaremap.entities.SquaremapOnlinePlayer;

import kotlin.Pair;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Players extends BaseCache<SquaremapOnlinePlayer> implements ILocatable<SquaremapOnlinePlayer> {
    private final SquaremapParser parser;

    @Setter private Residents residents;

    public Players(@NotNull SquaremapParser parser, CacheOptions options) {
        super(options);
        this.parser = parser;
    }

    @Override
    protected Map<String, SquaremapOnlinePlayer> fetchCacheData() {
        this.parser.parsePlayerData();
        return this.parser.getOnlinePlayers();
    }

    /**
     * Similar to {@link #getAll()}, this method gets all the players but only includes those
     * that match a certain residential status indicated by the {@code hasTown} parameter.<br><br>
     *
     * <b>NOTE:</b> If the intention is to call this method twice, once for townless and another for towned players,
     * consider using {@link #getSorted()} instead to sort them in a single forEach pass and avoid unnecessary operations.
     * @param hasTown The residential status to filter by. True for residents, false for townless.
     * @return The filtered map of online players that pass the hasTown value.
     */
    @SuppressWarnings("unused")
    public Map<String, SquaremapOnlinePlayer> getByResidency(boolean hasTown) {
        // Dont care abt value, we only need to know if they exist.
        Set<String> residents = this.residents.getAll().keySet();
        Map<String, SquaremapOnlinePlayer> ops = getAll();

        return ops.entrySet().stream() // Amt of online players will almost always be too little to warrant parallelism.
            .filter(opEntry -> residents.contains(opEntry.getKey()) == hasTown) // O(1) lookup. Key is the player's name.
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)); // This is why I hate Java.
    }

    /**
     * Gets online players and sorts them into two seperate maps in a single pass.<br>
     * A new {@link Pair} is returned, where the first contains players with a town and the second without.
     */
    @SuppressWarnings("unused")
    public Pair<Map<String, SquaremapOnlinePlayer>, Map<String, SquaremapOnlinePlayer>> getSorted() {
        Set<String> residentNames = this.residents.getAll().keySet();
        Map<String, SquaremapOnlinePlayer> ops = getAll();

        Map<String, SquaremapOnlinePlayer> townless = new HashMap<>();
        Map<String, SquaremapOnlinePlayer> onlineResidents = new HashMap<>();

        ops.forEach((opName, op) -> {
            boolean isResident = residentNames.contains(opName);
            (isResident ? onlineResidents : townless).put(opName, op);
        });

        return new Pair<>(onlineResidents, townless);
    }

    public Map<String, SquaremapOnlinePlayer> getNearby(Integer xCoord, Integer zCoord, Integer radius) {
        return getNearbyEntities(getAll(), xCoord, zCoord, radius);
    }

    public Map<String, SquaremapOnlinePlayer> getNearby(Integer xCoord, Integer zCoord, Integer xRadius, Integer zRadius) {
        return getNearbyEntities(getAll(), xCoord, zCoord, xRadius, zRadius);
    }

    public Map<String, SquaremapOnlinePlayer> getNearby(@NotNull SquaremapOnlinePlayer p, Integer xRadius, Integer zRadius) {
        SquaremapLocation playerLoc = p.getLocation();
        if (playerLoc.isMapCenter()) return new HashMap<>();

        Map<String, SquaremapOnlinePlayer> nearby = getNearbyEntities(getAll(), playerLoc.getX(), playerLoc.getZ(), xRadius, zRadius);
        nearby.remove(p.getName());

        return nearby;
    }

    public Map<String, SquaremapOnlinePlayer> getNearby(@NotNull SquaremapLocation location, Integer xRadius, Integer zRadius) {
        if (!location.isValidPoint()) return new HashMap<>();
        return getNearbyEntities(getAll(), location.getX(), location.getZ(), xRadius, zRadius);
    }
}
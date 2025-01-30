package io.github.emcw.squaremap.api;

import com.github.benmanes.caffeine.cache.Cache;

import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;

import io.github.emcw.squaremap.SquaremapParser;
import io.github.emcw.squaremap.entities.SquaremapLocation;

import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.squaremap.entities.SquaremapOnlinePlayer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Players extends BaseCache<SquaremapOnlinePlayer> implements ILocatable<SquaremapOnlinePlayer> {
    SquaremapParser parser;
    Residents residents;

    public Players(@NotNull SquaremapParser parser, Residents residents, CacheOptions options) {
        super(options);

        this.parser = parser;
        this.residents = residents;

        buildCache();
    }

    @Override
    protected void updateCache(Boolean force) {
        // We aren't forcing an update, and not expired.
        if (!cacheIsEmpty() && !force) return;

        // Parse player data into usable Player objects.
        this.parser.parsePlayerData();

        // TODO: Replacing one cache with another. Is this redundant?
        Cache<String, SquaremapOnlinePlayer> ops = this.parser.getOnlinePlayers();

        // Make sure we're using valid data to populate the cache with.
        if (ops == null) return;
        if (ops.asMap().isEmpty()) return;

        setCache(ops);
    }

    public Map<String, SquaremapOnlinePlayer> nearby(Integer xCoord, Integer zCoord, Integer radius) {
        return getNearby(getAll(), xCoord, zCoord, radius);
    }

    public Map<String, SquaremapOnlinePlayer> nearby(Integer xCoord, Integer zCoord, Integer xRadius, Integer zRadius) {
        return getNearby(getAll(), xCoord, zCoord, xRadius, zRadius);
    }

    public Map<String, SquaremapOnlinePlayer> nearby(@NotNull SquaremapOnlinePlayer p, Integer xRadius, Integer zRadius) {
        SquaremapLocation playerLoc = p.getLocation();
        if (playerLoc.isDefault()) return new HashMap<>();

        Map<String, SquaremapOnlinePlayer> nearby = getNearby(getAll(), playerLoc.getX(), playerLoc.getZ(), xRadius, zRadius);
        nearby.remove(p.getName());

        return nearby;
    }

    public Map<String, SquaremapOnlinePlayer> nearby(@NotNull SquaremapLocation location, Integer xRadius, Integer zRadius) {
        if (!location.valid()) return new HashMap<>();
        return getNearby(getAll(), location.getX(), location.getZ(), xRadius, zRadius);
    }

    public Map<String, SquaremapOnlinePlayer> townless() {
        Map<String, SquaremapOnlinePlayer> ops = getAll();
        Set<String> residents = this.residents.getAll().keySet(); // Dont care abt value, we only need to know if they exist.

        // Single pass over entrySet is preferred in this case since we need both key and value.
        return ops.entrySet().stream() // Amt of online players will almost always be too little to warrant parallelism.
            .filter(opEntry -> residents.contains(opEntry.getKey())) // O(1) lookup. Key is the player's name.
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)); // This is why I hate Java.
    }

    /**
     * Equivalent to {@code Players.all().getOrDefault("PlayerName", null)}.
     */
    @Nullable
    public SquaremapOnlinePlayer getOnlinePlayer(String playerName) {
        return getAll().getOrDefault(playerName, null);
    }
}
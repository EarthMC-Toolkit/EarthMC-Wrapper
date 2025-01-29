package io.github.emcw.squaremap.api;

import com.github.benmanes.caffeine.cache.Cache;

import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;

import io.github.emcw.squaremap.SquaremapParser;
import io.github.emcw.squaremap.entities.SquaremapLocation;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.squaremap.entities.SquaremapOnlinePlayer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.emcw.utils.GsonUtil.*;

@SuppressWarnings("unused")
public class Players extends BaseCache<SquaremapOnlinePlayer> implements ILocatable<SquaremapOnlinePlayer> {
    SquaremapParser parser;
    Residents residents;

    public Players(@NotNull SquaremapParser parser, Residents residents, CacheOptions options) {
        super(options);

        this.parser = parser;
        this.residents = residents;

        setUpdater(this::forceUpdateCache); // Cache will be force updated each time update.run() is called.
        buildCache();
    }

    public void tryUpdateCache() {
        tryExpireCache();
        updateCache(false);
    }

    public void forceUpdateCache() {
        updateCache(true);
    }

    void updateCache(Boolean force) {
        // We aren't forcing an update, and not expired.
        if (!cacheIsEmpty() && !force) return;

        // Parse player data into usable Player objects.
        parser.parsePlayerData();
        Cache<String, SquaremapOnlinePlayer> ops = parser.getOnlinePlayers(); // TODO: Replacing one cache with another. Just use parser caches directly?

        // Make sure we're using valid data to populate the cache with.
        if (ops == null) return;
        if (ops.asMap().isEmpty()) return;

        setCache(ops);
    }

    @Override
    public SquaremapOnlinePlayer single(String name) throws MissingEntryException {
        tryUpdateCache();
        return super.single(name);
    }

    @Override
    public Map<String, SquaremapOnlinePlayer> get(String @NotNull ... keys) {
        tryUpdateCache();
        return super.get(keys);
    }

    public Map<String, SquaremapOnlinePlayer> all() {
        tryUpdateCache();
        return cache.asMap();
    }

    public Map<String, SquaremapOnlinePlayer> nearby(Integer xCoord, Integer zCoord, Integer radius) {
        return getNearby(all(), xCoord, zCoord, radius);
    }

    public Map<String, SquaremapOnlinePlayer> nearby(Integer xCoord, Integer zCoord, Integer xRadius, Integer zRadius) {
        return getNearby(all(), xCoord, zCoord, xRadius, zRadius);
    }

    public Map<String, SquaremapOnlinePlayer> nearby(@NotNull SquaremapOnlinePlayer p, Integer xRadius, Integer zRadius) {
        SquaremapLocation playerLoc = p.getLocation();

        if (playerLoc.isDefault()) return Map.of();

        Map<String, SquaremapOnlinePlayer> nearby = getNearby(all(), playerLoc.getX(), playerLoc.getZ(), xRadius, zRadius);
        nearby.remove(p.getName());

        return nearby;
    }

    public Map<String, SquaremapOnlinePlayer> nearby(@NotNull SquaremapLocation location, Integer xRadius, Integer zRadius) {
        if (!location.valid()) return Map.of();
        return getNearby(all(), location.getX(), location.getZ(), xRadius, zRadius);
    }

    public Map<String, SquaremapOnlinePlayer> townless() {
        Set<String> ops = all().keySet();

        Stream<SquaremapOnlinePlayer> players = streamValues(all()).filter(p -> !ops.contains(p));
        return players.collect(Collectors.toMap(SquaremapOnlinePlayer::getName, Function.identity()));
    }

    /**
     * Equivalent to {@code Players.all().getOrDefault("PlayerName", null)}.
     */
    @Nullable
    public SquaremapOnlinePlayer getOnlinePlayer(String playerName) {
        return all().getOrDefault(playerName, null);
    }
}
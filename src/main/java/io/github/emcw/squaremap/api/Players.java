package io.github.emcw.squaremap.api;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.gson.JsonObject;

import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;

import io.github.emcw.squaremap.SquaremapParser;
import io.github.emcw.squaremap.entities.SquaremapLocation;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.squaremap.entities.SquaremapOnlinePlayer;
import io.github.emcw.squaremap.entities.SquaremapResident;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
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

        setUpdater(this::forceUpdate);
        buildCache();
    }

    public void tryUpdate() {
        tryExpire();
        updateCache(false);
    }

    public void forceUpdate() {
        updateCache(true);
    }

    void updateCache(Boolean force) {
        // We aren't forcing an update, and not expired.
        if (!empty() && !force) return;

        // Parse player data into usable Player objects.
        parser.parsePlayerData();
        Cache<String, SquaremapOnlinePlayer> players = parser.getPlayers();

        // Make sure we're using valid data to populate the cache with.
        if (players == null) return;
        if (players.asMap().isEmpty()) return;

        setCache(players);
    }

    @Override
    public SquaremapOnlinePlayer single(String name) throws MissingEntryException {
        tryUpdate();
        return super.single(name);
    }

    @Override
    public Map<String, SquaremapOnlinePlayer> get(String @NotNull ... keys) {
        tryUpdate();
        return super.get(keys);
    }

    /**
     * Retreives all the online players, and merges them with residents.<br><br>
     * Townless players will strictly be {@link SquaremapOnlinePlayer}, while residents can be {@link SquaremapResident}.
     * You can check if a value is a resident by using the {@link SquaremapPlayer#isResident()} method.
     * @return A map of players where the key is the player name and value is the merged player object.
     */
    public Map<String, SquaremapPlayer> all() {
        return mergeWith(residents.all());
    }

    public Map<String, SquaremapOnlinePlayer> online() {
        tryUpdate();
        return cache.asMap();
    }

    public Map<String, SquaremapOnlinePlayer> nearby(Integer xCoord, Integer zCoord, Integer radius) {
        return getNearby(online(), xCoord, zCoord, radius);
    }

    public Map<String, SquaremapOnlinePlayer> nearby(Integer xCoord, Integer zCoord, Integer xRadius, Integer zRadius) {
        return getNearby(online(), xCoord, zCoord, xRadius, zRadius);
    }

    public Map<String, SquaremapOnlinePlayer> nearby(@NotNull SquaremapOnlinePlayer p, Integer xRadius, Integer zRadius) {
        SquaremapLocation playerLoc = p.getLocation();

        if (playerLoc.isDefault()) return Map.of();

        Map<String, SquaremapOnlinePlayer> nearby = getNearby(online(), playerLoc.getX(), playerLoc.getZ(), xRadius, zRadius);
        nearby.remove(p.getName());

        return nearby;
    }

    public Map<String, SquaremapOnlinePlayer> nearby(@NotNull SquaremapLocation location, Integer xRadius, Integer zRadius) {
        if (!location.valid()) return Map.of();
        return getNearby(online(), location.getX(), location.getZ(), xRadius, zRadius);
    }

    private @NotNull Map<String, SquaremapOnlinePlayer> mergeWith(Map<String, SquaremapResident> residents) {
        Map<String, SquaremapOnlinePlayer> ops = online();
        Map<String, SquaremapPlayer> merged = new ConcurrentHashMap<>(ops);

        // Loop through residents in parallel
        streamValues(residents).forEach(res -> {
            String resName = res.getName();
            JsonObject resObj = asTree(res);

            SquaremapOnlinePlayer found = ops.get(resName);
            SquaremapResident player = found == null ? new SquaremapResident(resObj) : new SquaremapResident(asTree(resObj), found);

            merged.put(resName, player);
        });

        // Remove null values from resulting map
        merged.values().removeIf(Objects::isNull);

        return merged;
    }

    public Map<String, SquaremapOnlinePlayer> townless() {
        Stream<SquaremapOnlinePlayer> players = streamValues(all()).filter(p -> !p.isSquaremapResident());
        return players.collect(Collectors.toMap(SquaremapOnlinePlayer::getName, Function.identity()));
    }

    @Nullable
    public SquaremapOnlinePlayer getOnline(String playerName) {
        Map<String, SquaremapOnlinePlayer> map = online();
        return map.getOrDefault(playerName, null);
    }
}
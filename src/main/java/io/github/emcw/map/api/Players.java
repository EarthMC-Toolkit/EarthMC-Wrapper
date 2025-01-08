package io.github.emcw.map.api;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.gson.JsonObject;
import io.github.emcw.EMCMap;
import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;

import io.github.emcw.map.entities.Location;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.map.entities.Player;
import io.github.emcw.map.entities.Resident;
import io.github.emcw.utils.parsers.SquaremapParser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.emcw.utils.Funcs.collectEntities;
import static io.github.emcw.utils.GsonUtil.*;

@SuppressWarnings("unused")
public class Players extends BaseCache<Player> implements ILocatable<Player> {
    Residents residents;

    public Players(@NotNull EMCMap mapInstance, CacheOptions options) {
        super(options);
        residents = mapInstance.Residents;

        setUpdater(this::forceUpdate);
        build();
    }

    public void tryUpdate() {
        tryExpire();
        updateCache(false);
    }

    public void forceUpdate() {
        updateCache(true);
    }

    public void updateCache(Boolean force) {
        // We aren't forcing an update, and not expired.
        if (!empty() && !force) return;

        // Parse player data into usable Player objects.
        SquaremapParser.parsePlayerData();
        Cache<String, Player> players = SquaremapParser.parsedPlayers();

        // Make sure we have data to use.
        if (players != null && !players.asMap().isEmpty()) {
            setCache(players);
        }
    }

    public Map<String, Player> all() {
        // Merge residents & online players (townless will not include keys 'town', 'nation' and 'rank')
        return mergeWith(residents.all());
    }

    @Override
    public Player single(String name) throws MissingEntryException {
        tryUpdate();
        return super.single(name);
    }

    @Override
    public Map<String, Player> get(String @NotNull ... keys) {
        tryUpdate();
        return super.get(keys);
    }

    public Map<String, Player> online() {
        tryUpdate();
        return cache.asMap();
    }

    public Map<String, Player> nearby(Integer xCoord, Integer zCoord, Integer radius) {
        return getNearby(online(), xCoord, zCoord, radius);
    }

    public Map<String, Player> nearby(Integer xCoord, Integer zCoord, Integer xRadius, Integer zRadius) {
        return getNearby(online(), xCoord, zCoord, xRadius, zRadius);
    }

    public Map<String, Player> nearby(@NotNull Player p, Integer xRadius, Integer zRadius) {
        Location playerLoc = p.getLocation();

        if (playerLoc.isDefault()) return Map.of();

        Map<String, Player> nearby = getNearby(online(), playerLoc.getX(), playerLoc.getZ(), xRadius, zRadius);
        nearby.remove(p.getName());

        return nearby;
    }

    public Map<String, Player> nearby(@NotNull Location location, Integer xRadius, Integer zRadius) {
        if (!location.valid()) return Map.of();
        return getNearby(online(), location.getX(), location.getZ(), xRadius, zRadius);
    }

    private @NotNull Map<String, Player> mergeWith(Map<String, Resident> residents) {
        Map<String, Player> ops = online();
        Map<String, Player> merged = new ConcurrentHashMap<>(ops);

        // Loop through residents in parallel
        streamValues(residents).forEach(res -> {
            String resName = res.getName();
            JsonObject resObj = asTree(res);

            Player found = ops.get(resName);
            Resident player = found == null ? new Resident(resObj) : new Resident(asTree(resObj), found);

            merged.put(resName, player);
        });

        // Remove null values from resulting map
        merged.values().removeIf(Objects::isNull);
        return merged;
    }

    public Map<String, Player> townless() {
        return collectEntities(streamValues(all()).filter(p -> !p.isResident()));
    }

    @Nullable
    public Player getOnline(String playerName) {
        Map<String, Player> map = online();
        return map.getOrDefault(playerName, null);
    }
}
package io.github.emcw.map;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.gson.JsonObject;
import io.github.emcw.caching.BaseCache;
import io.github.emcw.core.EMCMap;
import io.github.emcw.entities.Location;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.entities.Player;
import io.github.emcw.entities.Resident;
import io.github.emcw.utils.DataParser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.emcw.utils.GsonUtil.*;

public class Players extends BaseCache<Player> implements ILocatable<Player> {
    private final EMCMap parent;

    public Players(EMCMap parent) {
        super(Duration.ofMillis(1500));
        this.parent = parent;
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        // We aren't forcing an update, and not expired.
        if (!cache.asMap().isEmpty() && !force) return;

        // Parse player data into usable Player objects.
        DataParser.parsePlayerData(parent.getMap());
        Cache<String, Player> players = DataParser.parsedPlayers();

        // Make sure we have data to use.
        if (!players.asMap().isEmpty())
            cache = players;
    }

    @Override
    public Map<String, Player> all() {
        // Merge residents & online players (townless will not include keys 'town', 'nation' and 'rank')
        return mergeWith(parent.Residents.all());
    }

    @Override
    public Player single(String name) throws MissingEntryException {
        updateCache();
        return super.single(name);
    }

    public Map<String, Player> get(String @NotNull ... keys) {
        updateCache();
        return super.get(keys);
    }

    public Map<String, Player> online() {
        updateCache();
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
        Map<String, Player> merged = new ConcurrentHashMap<>(online());

        // Loop through residents in parallel
        streamValues(residents).forEach(res -> {
            String resName = res.getName();
            JsonObject resObj = asTree(res).getAsJsonObject();

            Player op = merged.get(resName);
            Player player = op == null ? new Resident(resObj) : new Resident(resObj, asTree(op).getAsJsonObject());

            merged.put(resName, player);
        });

        // Remove null values from resulting map
        merged.values().removeIf(Objects::isNull);
        return merged;
    }

    public Map<String, Player> townless() {
        return difference(mapToArr(online()), mapToArr(parent.Residents.all()));
    }

    @Nullable
    public Player getOnline(String playerName) {
        Map<String, Player> map = online();
        return map.getOrDefault(playerName, null);
    }
}

package io.github.emcw.map;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.gson.JsonObject;
import io.github.emcw.caching.BaseCache;
import io.github.emcw.core.EMCMap;
import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.entities.Player;
import io.github.emcw.entities.Resident;
import io.github.emcw.utils.DataParser;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.emcw.utils.GsonUtil.*;

public class Players extends BaseCache<Player> implements ILocatable<Player> {
    private final EMCMap parent;

    public Players(EMCMap parent) {
        super(Duration.ofSeconds(2));
        this.parent = parent;
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        if (cache != null && !force) return;

        // Parse player data into usable Player objects.
        DataParser.parsePlayerData(parent.getMap());

        Map<String, Player> players = DataParser.parsedPlayers();
        if (!players.isEmpty()) cache.putAll(players);
    }

    public Map<String, Player> nearby(Integer xCoord, Integer zCoord, Integer radius) {
        return getNearby(online(), xCoord, zCoord, radius);
    }

    public Map<String, Player> nearby(Integer xCoord, Integer zCoord, Integer xRadius, Integer zRadius) {
        return getNearby(online(), xCoord, zCoord, xRadius, zRadius);
    }

    public Map<String, Player> online() {
        return cache.asMap();
    }

    @Override
    public Map<String, Player> all() {
        // Merge residents & online players (townless will not include keys 'town', 'nation' and 'rank')
        return mergeWith(parent.Residents.all());
    }

    private Map<String, Player> mergeWith(Map<String, Resident> residents) {
        Map<String, Player> merged = new ConcurrentHashMap<>(cache.asMap());

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
        Cache<String, Resident> residents = parent.Residents.cache;
        return difference(mapToArr(cache.asMap()), mapToArr(residents.asMap()));
    }

    @Nullable
    public Player getOnline(String playerName) {
        Map<String, Player> map = cache.asMap();
        Player pl = null;

        if (!map.isEmpty()) {
            for (Player op : map.values()) {
                if (Objects.equals(op.getName(), playerName)) {
                    pl = op;
                    break;
                }
            }
        }

        return pl;
    }
}

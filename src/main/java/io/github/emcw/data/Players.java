package io.github.emcw.data;

import io.github.emcw.core.EMCMap;
import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.objects.Player;
import io.github.emcw.objects.Resident;
import io.github.emcw.utils.DataParser;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

import static io.github.emcw.utils.GsonUtil.*;

public class Players extends Assembly<Player> implements ILocatable<Player> {
    private final EMCMap parent;

    public Players(EMCMap parent) {
        this.parent = parent;
        updateCache(true);
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        if (cache != null && !force) return;

        // Parse player data into usable Player objects.
        DataParser.parsePlayerData(parent.getMap());

        Map<String, Player> players = DataParser.playersAsMap();
        if (!players.isEmpty()) cache = players;
    }

    public Map<String, Player> nearby(Integer xCoord, Integer zCoord, Integer radius) {
        return getNearby(cache, xCoord, zCoord, radius);
    }

    public Map<String, Player> nearby(Integer xCoord, Integer zCoord, Integer xRadius, Integer zRadius) {
        return getNearby(cache, xCoord, zCoord, xRadius, zRadius);
    }

    public Map<String, Player> townless() {
        Map<String, Resident> residents = parent.Residents.cache;
        return difference(mapToArr(cache), mapToArr(residents));
    }

    @Nullable
    public Player getOnline(String playerName) {
        Player pl = null;

        if (!cache.isEmpty()) {
            for (Player op : cache.values()) {
                if (Objects.equals(op.getName(), playerName)) {
                    pl = op;
                    break;
                }
            }
        }

        return pl;
    }
}

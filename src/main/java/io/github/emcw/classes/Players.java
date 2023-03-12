package io.github.emcw.classes;

import io.github.emcw.core.EMCMap;
import io.github.emcw.interfaces.Collective;
import io.github.emcw.objects.Player;
import io.github.emcw.objects.Resident;
import io.github.emcw.utils.DataParser;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.JsonObject;

import static io.github.emcw.utils.GsonUtil.*;

public class Players implements Collective<Player> {
    private final EMCMap parent;
    protected Map<String, Player> cache = null;

    public Players(EMCMap parent) {
        this.parent = parent;
        updateCache(true);
    }

    public Player single(JsonObject p) {
        String name = keyAsStr(p, "name");
        return single(name);
    }

    public Player single(String playerName) {
        updateCache();
        return Collective.super.single(playerName, cache);
    }

    public List<Player> all() {
        updateCache();
        return Collective.super.all(cache);
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        if (cache != null && !force) return;

        // Parse player data into usable Player objects.
        DataParser.parsePlayerData(parent.getMap());
        this.cache = DataParser.playersAsMap(DataParser.getPlayers());
    }

    public Map<String, Player> townless() {
        Map<String, Resident> residents = parent.Residents.cache;
        return difference(mapToArr(cache), mapToArr(residents), "name");
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

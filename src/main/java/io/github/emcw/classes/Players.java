package io.github.emcw.classes;

import io.github.emcw.core.EMCMap;
import io.github.emcw.interfaces.Collective;
import io.github.emcw.objects.Player;
import io.github.emcw.objects.Resident;
import io.github.emcw.utils.DataParser;

import org.jetbrains.annotations.Nullable;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.JsonObject;

import static io.github.emcw.utils.GsonUtil.*;

public class Players implements Collective<Player> {
    private EMCMap parent;

    @Getter
    protected Map<String, Player> cache;

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
        return Collective.super.single(playerName, getCache());
    }

    public List<Player> all() {
        updateCache();
        return Collective.super.all(getCache());
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        if (getCache() != null && !force) return;

        // Parse player data into usable Player objects.
        DataParser.parsePlayerData(parent.getMap());
        this.cache = DataParser.playersAsMap(DataParser.getPlayers());
    }

    public Map<String, Player> townless() {
        Map<String, Resident> residents = parent.Residents.cache;
        return arrToMap(difference(mapToArr(getCache()), mapToArr(residents)), "name");
    }

    @Nullable
    public Player getOnline(String playerName) {
        Player pl = null;

        if (!getCache().isEmpty()) {
            for (Player op : getCache().values()) {
                if (Objects.equals(op.getName(), playerName)) {
                    pl = op;
                    break;
                }
            }
        }

        return pl;
    }
}

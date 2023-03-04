package io.github.emcw.classes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.emcw.interfaces.Collective;
import io.github.emcw.objects.Player;
import io.github.emcw.utils.API;
import io.github.emcw.utils.DataParser;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.github.emcw.utils.GsonUtil.keyAsStr;

public class Players implements Collective<Player> {
    Map<String, Player> cache;
    String map;

    public Players(String mapName) {
        this.map = mapName;
        updateCache(true);
    }

    public static List<Player> fromArray(JsonArray arr) {
        return arr.asList().stream().parallel()
                .map(p -> new Player(p.getAsJsonObject()))
                .collect(Collectors.toList());
    }

    public Player single(JsonObject p) {
        String name = keyAsStr(p, "name");
        return single(name);
    }

    public Player single(String playerName) {
        return Collective.super.single(playerName, this.cache);
    }

    public List<Player> all() {
        return Collective.super.all(this.cache);
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        if (this.cache != null && !force) return;

        // Parse player data into usable Player objects.
        DataParser.parsePlayerData(this.map);
        this.cache = DataParser.playersAsMap(DataParser.getPlayers());
    }

    @Nullable
    public Player getOnline(String playerName) {
        Player pl = null;

        if (!this.cache.isEmpty()) {
            for (Player op : this.cache.values()) {
                if (Objects.equals(op.name, playerName)) {
                    pl = op;
                    break;
                }
            }
        }

        return pl;
    }
}

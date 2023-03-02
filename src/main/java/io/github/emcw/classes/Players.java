package io.github.emcw.classes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.emcw.interfaces.Collective;
import io.github.emcw.objects.Player;
import io.github.emcw.utils.API;
import io.github.emcw.utils.DataParser;

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

    public List<Player> all() {
        return Collective.super.all(this.cache);
    }

    public Player getOnline(JsonObject p) {
        String name = keyAsStr(p, "name");
        return getOnline(name);
    }

    public Player getOnline(String playerName) {
        JsonArray ops = API.playerData(this.map).getAsJsonArray("players");
        JsonObject pl = new JsonObject();

        if (!ops.isEmpty()) {
            for (JsonElement op : ops) {
                JsonObject cur = op.getAsJsonObject();

                if (Objects.equals(keyAsStr(cur, "name"), playerName)) {
                    pl = cur;
                    break;
                }
            }
        }

        return new Player(pl);
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
}

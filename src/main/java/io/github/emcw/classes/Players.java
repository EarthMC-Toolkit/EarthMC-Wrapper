package io.github.emcw.classes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.emcw.interfaces.Collective;
import io.github.emcw.objects.Player;
import io.github.emcw.utils.API;
import io.github.emcw.utils.GsonUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Players implements Collective<Player> {
    Map<String, Player> cache;
    String map;

    public Players(String mapName) {
        this.map = mapName;
    }

    public static List<Player> fromArray(JsonArray arr) {
        return arr.asList().stream().parallel()
                .map(p -> new Player(p.getAsJsonObject()))
                .collect(Collectors.toList());
    }

    public JsonArray online() {
        return API.playerData(this.map).getAsJsonArray("players");
    }

    public Player getOnline(JsonObject p) {
        String name = GsonUtil.keyAsStr(p, "name");
        return getOnline(name);
    }

    public Player getOnline(String playerName) {
        JsonArray ops = online();
        JsonObject pl = new JsonObject();

        if (!ops.isEmpty()) {
            for (JsonElement op : ops) {
                JsonObject cur = op.getAsJsonObject();

                if (Objects.equals(GsonUtil.keyAsStr(cur, "name"), playerName)) {
                    pl = cur;
                    break;
                }
            }
        }

        return new Player(pl);
    }
}

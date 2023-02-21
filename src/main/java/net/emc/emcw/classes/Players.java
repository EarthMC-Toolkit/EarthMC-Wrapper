package net.emc.emcw.classes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.emc.emcw.interfaces.Collective;
import net.emc.emcw.objects.Player;
import net.emc.emcw.utils.API;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.emc.emcw.utils.GsonUtil.keyAsStr;

public class Players implements Collective<Player> {
    Map<String, Player> cache;
    String map;

    public Players(String mapName) {
        this.map = mapName;
    }

    public static List<Player> fromArray(JsonArray arr) {
        return arr.asList().stream()
                .map(p -> new Player(p.getAsJsonObject()))
                .collect(Collectors.toList());
    }

    public JsonArray online() {
        return API.playerData(this.map).getAsJsonArray("players");
    }

    public Player getOnline(JsonObject p) {
        String name = keyAsStr(p, "name");
        return getOnline(name);
    }

    public Player getOnline(String playerName) {
        JsonArray ops = online();
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
}

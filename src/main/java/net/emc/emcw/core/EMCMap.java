package net.emc.emcw.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.emc.emcw.objects.Player;
import net.emc.emcw.utils.API;

import java.util.Objects;

import static net.emc.emcw.utils.GsonUtil.keyAsStr;

public class EMCMap {
    String map;

    EMCMap(String mapName) {
        this.map = mapName;
    }

    public JsonArray onlinePlayers() {
        return API.playerData(this.map).getAsJsonArray("players");
    }

    public Player getOnlinePlayer(JsonObject p) {
        String name = keyAsStr(p, "name");
        return getOnlinePlayer(name);
    }

    public Player getOnlinePlayer(String playerName) {
        JsonArray ops = onlinePlayers();
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

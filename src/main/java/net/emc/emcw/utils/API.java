package net.emc.emcw.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.concurrent.CompletableFuture;

import net.emc.emcw.exceptions.APIException;

public class API {

//    public static JsonObject getOnlinePlayer(String name) {
//        JsonArray ops = getOnlinePlayers().join().getAsJsonArray();
//        JsonObject pl = new JsonObject();
//
//        if (!ops.isEmpty()) {
//            for (JsonElement op : ops) {
//                JsonObject cur = op.getAsJsonObject();
//
//                if (Objects.equals(cur.get("name").getAsString(), name)) {
//                    pl = cur;
//                    break;
//                }
//            }
//        }
//
//        return pl;
//    }

    public static JsonArray playerData(String mapName) {
        String endpoint = Request.getEndpoints()
                .get("players").getAsJsonObject()
                .get(mapName).getAsString();

        JsonObject pData = (JsonObject) CompletableFuture.supplyAsync(() -> {
            try { return new Request(endpoint).body(); }
            catch (APIException e) { return null; }
        }).join();

        return pData.getAsJsonArray("players");
    }
}


package net.emc.emcw.utils;

import com.google.gson.JsonObject;

import java.util.concurrent.CompletableFuture;

import net.emc.emcw.exceptions.APIException;

public class API {
    public static CompletableFuture<JsonObject> get(String map, String key) {
        String endpoint = Request.getEndpoints()
                .get(key).getAsJsonObject()
                .get(map).getAsString();

        return CompletableFuture.supplyAsync(() -> {
            try { return new Request(endpoint).body(); }
            catch (APIException e) { return null; }
        });
    }

    public static JsonObject playerData(String mapName) {
        return get(mapName, "players").join();
    }

    public static JsonObject mapData(String mapName) {
        return get(mapName, "map").join()
                .getAsJsonObject("sets")
                .getAsJsonObject("townyPlugin.markerset")
                .getAsJsonObject("areas");
    }
}


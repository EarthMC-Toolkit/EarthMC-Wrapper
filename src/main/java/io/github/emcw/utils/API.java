package io.github.emcw.utils;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class API {
    public static CompletableFuture<JsonObject> get(String map, String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String endpoint = Request.getEndpoints().getIfPresent(key).get(map).getAsString();
                return Request.send(endpoint);
            }
            catch (Exception e) {
                System.out.println("Exception occurred!\n" + e.getMessage());
                return null;
            }
        });
    }

    public static JsonObject playerData(String mapName) {
        return get(mapName, "players").join();
    }

    public static JsonObject mapData(String mapName) {
        return get(mapName, "map").join().getAsJsonObject("sets")
                .getAsJsonObject("townyPlugin.markerset")
                .getAsJsonObject("areas");
    }
}
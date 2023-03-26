package io.github.emcw.utils;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class API {
    @Contract("_, _ -> new")
    public static @NotNull CompletableFuture<JsonObject> get(String map, String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject endpoint = Request.getEndpoints().getIfPresent(key);
                if (endpoint != null) return Request.send(endpoint.get(map).getAsString());

                throw new NullPointerException("Error fetching " + key + "! Received `null` as endpoint URL.");
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
package io.github.emcw.utils;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Utility class for interacting with the <a href="earthmc.net/map/aurora/">EarthMC Dynmap</a> asynchronously.<br><br>
 * <b>Note:</b>
 * <br>This class is used internally to obtain fresh data, you should never need to use it directly.
 */
public final class API {
    private API() {}

    @Contract("_, _ -> new")
    private static @NotNull CompletableFuture<JsonObject> get(String map, String key) {
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

    public static JsonObject configData(String mapName) {
        return get(mapName, "config").join();
    }

    public static JsonObject playerData(String mapName) {
        return get(mapName, "players").join();
    }

    public static JsonObject mapData(String mapName) {
        CompletableFuture<JsonObject> data = get(mapName, "map");

        try {
            return data.join().getAsJsonObject("sets")
                .getAsJsonObject("townyPlugin.markerset")
                .getAsJsonObject("areas");
        }
        catch (Exception e) {
            throw new NullPointerException("" +
                "Error fetching " + mapName + " map data!\n" +
                "Received response may be incorrectly formatted."
            );
        }
    }
}
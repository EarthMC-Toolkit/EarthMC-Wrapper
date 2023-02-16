package net.emc.emcw.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.concurrent.CompletableFuture;

import net.emc.emcw.exceptions.APIException;

public class API {

    public static CompletableFuture<JsonObject> playerData(String mapName) {
        String endpoint = Request.getEndpoints()
                .get("players").getAsJsonObject()
                .get(mapName).getAsString();

        return CompletableFuture.supplyAsync(() -> {
            try { return new Request(endpoint).body(); }
            catch (APIException e) { return null; }
        });
    }
}


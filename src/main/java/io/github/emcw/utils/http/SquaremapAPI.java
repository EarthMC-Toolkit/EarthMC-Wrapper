package io.github.emcw.utils.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static io.github.emcw.utils.GsonUtil.arrAsStream;
import static io.github.emcw.utils.GsonUtil.keyAsStr;

public final class SquaremapAPI {
    private SquaremapAPI() {
    }

    @Contract("_ -> new")
    private static @NotNull CompletableFuture<JsonElement> get(String type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject endpoint = Request.getEndpoints().getIfPresent("squaremap");
                if (endpoint != null) return Request.send(endpoint.get(type).getAsString());

                throw new NullPointerException("Error fetching " + "squaremap" + "! Received `null` as endpoint URL.");
            } catch (Exception e) {
                System.out.println("Exception occurred!\n" + e.getMessage());
                return null;
            }
        });
    }

    public static JsonObject playerData() {
        return get("players").join().getAsJsonObject();
    }

    public static JsonArray mapData() {
        CompletableFuture<JsonElement> data = get("map");

        try {
          JsonArray markersets = data.join().getAsJsonArray();
          JsonElement towny = arrAsStream(markersets)
              .map(JsonElement::getAsJsonObject)
              .filter(el -> Objects.equals(keyAsStr(el, "id"), "towny"))
              .findFirst()
              .orElseThrow();

          return towny.getAsJsonObject().get("markers").getAsJsonArray();
        } catch (Exception e) {
            System.out.println(
                "Error fetching Aurora map data!\n" +
                "Received response may be incorrectly formatted."
            );
        }

        return new JsonArray();
    }
}
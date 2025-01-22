package io.github.emcw.squaremap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.emcw.utils.http.Endpoints;
import io.github.emcw.utils.http.JSONRequest;

import java.util.Objects;

import static io.github.emcw.utils.GsonUtil.arrAsStream;
import static io.github.emcw.utils.GsonUtil.keyAsStr;

public final class SquaremapAPI {
    private SquaremapAPI() {
    }

    private static JsonElement get(String type) {
        JsonObject endpoint = Endpoints.get().getIfPresent("squaremap");
        if (endpoint != null) return JSONRequest.sendGet(endpoint.get(type).getAsString());

        throw new NullPointerException("Received `null` as endpoint URL.");
    }

    public static JsonObject playerData() {
        try {
            return get("players").getAsJsonObject();
        } catch(Exception e) {
            System.out.println(
                "Error fetching Squaremap player data!\n" +
                "Received response may be incorrectly formatted.\n\n" + e.getMessage()
            );
        }

        return new JsonObject();
    }

    public static JsonArray mapData() {
        try {
            JsonArray data = get("map").getAsJsonArray();
            JsonElement towny = arrAsStream(data)
                  .map(JsonElement::getAsJsonObject)
                  .filter(el -> Objects.equals(keyAsStr(el, "id"), "towny"))
                  .findFirst()
                  .orElseThrow();

            return towny.getAsJsonObject().get("markers").getAsJsonArray();
        } catch (Exception e) {
            System.out.println(
                "Error fetching Squaremap map data!\n" +
                "Received response may be incorrectly formatted.\n\n" + e.getMessage()
            );
        }

        return new JsonArray();
    }
}
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
    private static JsonElement getDataFromEndpoint(String mapName, String endpointKey) {
        JsonObject mapObj = Endpoints.get().getOrDefault(mapName, null);
        if (mapObj == null) {
            throw new NullPointerException("Could not find key in endpoints obj: " + mapName);
        }

        JsonObject squaremapEndpoints = mapObj.get("squaremap").getAsJsonObject();
        if (squaremapEndpoints == null) {
            throw new NullPointerException("Could not find key in endpoints obj: " + mapName + "/squaremap");
        }

        String url = keyAsStr(squaremapEndpoints, endpointKey);
        if (url == null || url.isBlank()) {
            throw new NullPointerException("Invalid value or missing key in endpoints obj: " + mapName + "/squaremap/" + endpointKey);
        }

        //System.out.println("Fetching data from: " + url);

        return JSONRequest.sendGet(url);
    }

    public static JsonObject playerData(String mapName) {
        try {
            return getDataFromEndpoint(mapName, "players").getAsJsonObject();
        } catch(Exception e) {
            System.err.println(
                "Error fetching Squaremap player data!\n" +
                "Received response may be incorrectly formatted.\n\n" + e.getMessage()
            );
        }

        return new JsonObject();
    }

    public static JsonArray mapData(String mapName) {
        try {
            JsonArray data = getDataFromEndpoint(mapName, "map").getAsJsonArray();
            JsonElement markerSet = arrAsStream(data)
                  .map(JsonElement::getAsJsonObject)
                  .filter(el -> Objects.equals(keyAsStr(el, "id"), "towny"))
                  .findFirst()
                  .orElseThrow();

            return markerSet.getAsJsonObject().get("markers").getAsJsonArray();
        } catch (Exception e) {
            System.err.println(
                "Error fetching Squaremap map data!\n" +
                "Received response may be incorrectly formatted.\n\n" + e.getMessage()
            );
        }

        return new JsonArray();
    }
}
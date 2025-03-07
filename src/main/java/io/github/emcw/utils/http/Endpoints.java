package io.github.emcw.utils.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SameParameterValue")
public class Endpoints {
    static final String ENDPOINTS_URL =
        "https://raw.githubusercontent.com/EarthMC-Toolkit/EarthMC-Wrapper/" +
        "main/src/main/resources/endpoints.json";

    static final Map<String, JsonObject> endpoints = new HashMap<>();

    @NotNull
    public static Map<String, JsonObject> get() {
        if (endpoints.isEmpty()) {
            JsonObject eps = fetchEndpoints();
            if (eps != null) {
                eps.asMap().forEach((k, v) -> endpoints.put(k, v.getAsJsonObject()));
            }
        }

        return endpoints;
    }

    @Nullable
    private static JsonObject fetchEndpoints() {
        // Try get endpoints from external source first.
        JsonElement el = JSONRequest.ASYNC.sendGet(ENDPOINTS_URL);
        if (el != null) return el.getAsJsonObject();

        // Fall back to built-in json file instead. Could be null if err occurs.
        return readJsonFromResource("endpoints.json");
    }

    @Nullable
    private static JsonObject readJsonFromResource(String resourcePath) {
        // Another reason I fucking despise Java.
        InputStream is = Endpoints.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            System.err.println("Could not find resource: " + resourcePath);
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
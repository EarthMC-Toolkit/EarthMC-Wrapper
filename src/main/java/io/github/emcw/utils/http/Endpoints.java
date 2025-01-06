package io.github.emcw.utils.http;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

public class Endpoints {
    static final String ENDPOINTS_URL = "https://raw.githubusercontent.com/EarthMC-Toolkit/EarthMC-NPM/main/src/endpoints.json";
    static final Cache<String, JsonObject> endpoints = Caffeine.newBuilder().build();

    public static Cache<String, JsonObject> get() {
        if (endpoints.asMap().isEmpty()) {
            JsonObject eps = update();
            if (eps != null) {
                eps.asMap().forEach((k, v) -> endpoints.put(k, v.getAsJsonObject()));
            }
        }

        return endpoints;
    }

    static @Nullable JsonObject update() {
        JsonElement el = JSONRequest.sendGet(ENDPOINTS_URL);
        if (el == null) {
            return null;
        }

        return el.getAsJsonObject();
    }
}
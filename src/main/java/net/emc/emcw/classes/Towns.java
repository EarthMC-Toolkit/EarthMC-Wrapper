package net.emc.emcw.classes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.emc.emcw.objects.Town;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Towns {
    static Map<String, Town> cache;

    public static Town single(String name) {
        return cache.get(name);
    }

    public List<Town> all() {
        return new ArrayList<>(cache.values());
    }

    public static List<Town> fromArray(JsonArray arr) {
        return arr.asList().stream()
                .map(p -> new Town(p.getAsJsonObject()))
                .collect(Collectors.toList());
    }

    void updateCache(JsonObject data) {
        // Send req to /marker_earth.json

        // Strip html and parse elements as Town object

        // Push town objects to cache
        // cache = fromArray();
    }
}
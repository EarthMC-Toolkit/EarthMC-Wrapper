package net.emc.emcw.classes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.emc.emcw.interfaces.Collective;
import net.emc.emcw.objects.Town;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Towns implements Collective<Town> {
    public Map<String, Town> cache = null;
    String map;

    public Towns(String mapName) {
        this.map = mapName;
    }

    public static List<Town> fromArray(JsonArray arr) {
        return arr.asList().stream()
                .map(p -> new Town(p.getAsJsonObject()))
                .collect(Collectors.toList());
    }

    public static void updateCache(JsonObject data) {
        // Send req to /marker_earth.json

        // Strip html and parse elements as Town object

        // Push town objects to cache
        // cache = fromArray();
    }
}
package io.github.emcw.classes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.emcw.objects.Nation;
import io.github.emcw.objects.Town;
import io.github.emcw.interfaces.Collective;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

import io.github.emcw.utils.DataParser;

public class Towns implements Collective<Town> {
    Map<String, Town> cache = null;

    @Setter @Getter
    String map;

    public Towns(String mapName) {
        setMap(mapName);
        updateCache(true);
    }

    public Town single(String key) throws NullPointerException {
        return Collective.super.single(key, this.cache);
    }

    public List<Town> all() {
        return Collective.super.all(this.cache);
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        if (this.cache != null && !force) return;

        // Parse map data into usable objects.
        DataParser.parse(map, false);

        // Convert to Town objects and use as cache.
        JsonObject towns = DataParser.get().getAsJsonObject("towns");
        this.cache = DataParser.townsAsMap(towns);
    }

    public static List<Town> fromArray(JsonArray arr) {
        return arr.asList().stream().parallel()
                .map(p -> new Town(p.getAsJsonObject()))
                .collect(Collectors.toList());
    }
}
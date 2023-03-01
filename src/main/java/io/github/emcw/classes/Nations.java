package io.github.emcw.classes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.emcw.interfaces.Collective;
import io.github.emcw.objects.Nation;
import io.github.emcw.utils.DataParser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Nations implements Collective<Nation> {
    Map<String, Nation> cache = null;
    String map;

    public Nations(String mapName) {
        this.map = mapName;
        updateCache(true);
    }

    public Nation single(String key) throws NullPointerException {
        return Collective.super.single(key, this.cache);
    }

    public List<Nation> all() {
        return Collective.super.all(this.cache);
    }

    static List<Nation> fromArray(JsonArray arr) {
        return arr.asList().stream().parallel()
                .map(p -> new Nation(p.getAsJsonObject()))
                .collect(Collectors.toList());
    }

    public void updateCache(Boolean force) {
        if (this.cache != null && !force) return;

        // Parse map data into usable objects.
        DataParser.parse(map, true);

        // Convert to Town objects and use as cache.
        JsonObject towns = DataParser.get().getAsJsonObject("nations");
        this.cache = DataParser.nationsAsMap(towns);
    }
}
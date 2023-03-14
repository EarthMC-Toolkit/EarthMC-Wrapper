package io.github.emcw.classes;

import io.github.emcw.core.EMCMap;
import io.github.emcw.interfaces.Collective;
import io.github.emcw.objects.Town;
import io.github.emcw.utils.DataParser;

import java.util.Map;

public class Towns implements Collective<Town> {
    private final EMCMap parent;
    protected Map<String, Town> cache = null;

    public Towns(EMCMap parent) {
        this.parent = parent;
        updateCache(true);
    }

    public Town single(String key) throws NullPointerException {
        updateCache();
        return single(key, cache);
    }

    public Map<String, Town> all() {
        updateCache();
        return cache;
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        if (cache != null && !force) return;

        // Parse map data into usable Town objects.
        DataParser.parseMapData(parent.getMap(), false, false);
        cache = DataParser.townsAsMap(DataParser.getTowns());
    }

//    public static List<Town> fromArray(JsonArray arr) {
//        return arrAsStream(arr)
//                .map(p -> new Town(p.getAsJsonObject()))
//                .collect(Collectors.toList());
//    }
}
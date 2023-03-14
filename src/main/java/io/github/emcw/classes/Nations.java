package io.github.emcw.classes;

import io.github.emcw.core.EMCMap;
import io.github.emcw.interfaces.Collective;
import io.github.emcw.objects.Nation;
import io.github.emcw.utils.DataParser;

import java.util.Map;

public class Nations implements Collective<Nation> {
    private final EMCMap parent;
    protected Map<String, Nation> cache = null;

    public Nations(EMCMap parent) {
        this.parent = parent;
        updateCache(true);
    }

    public Nation single(String key) throws NullPointerException {
        updateCache(false);
        return single(key, this.cache);
    }

    public Map<String, Nation> all() {
        updateCache(false);
        return cache;
    }

//    static List<Nation> fromArray(JsonArray arr) {
//        return arrAsStream(arr)
//                .map(p -> new Nation(p.getAsJsonObject()))
//                .collect(Collectors.toList());
//    }

    public void updateCache(Boolean force) {
        if (this.cache != null && !force) return;

        // Parse map data into usable Nation objects.
        DataParser.parseMapData(parent.getMap(), true, false);
        this.cache = DataParser.nationsAsMap(DataParser.getNations());
    }
}
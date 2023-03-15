package io.github.emcw.classes;

import io.github.emcw.core.EMCMap;
import io.github.emcw.interfaces.IMap;
import io.github.emcw.objects.Nation;
import io.github.emcw.utils.DataParser;

import java.util.Map;

public class Nations implements IMap<Nation> {
    private final EMCMap parent;
    protected Map<String, Nation> cache = null;

    public Nations(EMCMap parent) {
        this.parent = parent;
        updateCache(true);
    }

    public Nation single(String key) {
        return single(key, all());
    }

    public Map<String, Nation> all() {
        updateCache(false);
        return cache;
    }

    public void updateCache(Boolean force) {
        if (cache != null && !force) return;

        // Parse map data into usable Nation objects.
        DataParser.parseMapData(parent.getMap(), true, false);
        cache = DataParser.nationsAsMap(DataParser.getNations());
    }
}
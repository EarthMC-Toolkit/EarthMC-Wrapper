package io.github.emcw.classes;

import io.github.emcw.core.EMCMap;
import io.github.emcw.interfaces.IMap;
import io.github.emcw.objects.Town;
import io.github.emcw.utils.DataParser;

import java.util.Map;

public class Towns implements IMap<Town> {
    private final EMCMap parent;
    protected Map<String, Town> cache = null;

    public Towns(EMCMap parent) {
        this.parent = parent;
        updateCache(true);
    }

    public Town single(String key) {
        return single(key, all());
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
        DataParser.parseMapData(parent.getMap(), false, true);
        cache = DataParser.townsAsMap(DataParser.getTowns());
    }
}
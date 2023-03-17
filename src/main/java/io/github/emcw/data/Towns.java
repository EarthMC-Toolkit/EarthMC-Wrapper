package io.github.emcw.data;

import io.github.emcw.core.EMCMap;
import io.github.emcw.objects.Town;
import io.github.emcw.utils.DataParser;

import java.util.Map;

public class Towns extends Assembly<Town> {
    private final EMCMap parent;

    public Towns(EMCMap parent) {
        this.parent = parent;
        updateCache(true);
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        if (cache != null && !force) return;

        // Parse map data into usable Town objects.
        DataParser.parseMapData(parent.getMap(), false, true);

        Map<String, Town> towns = DataParser.townsAsMap();
        if (!towns.isEmpty()) cache = towns;
    }
}
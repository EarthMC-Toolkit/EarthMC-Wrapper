package io.github.emcw.map;

import io.github.emcw.core.EMCMap;
import io.github.emcw.objects.Nation;
import io.github.emcw.utils.DataParser;

import java.util.Map;

public class Nations extends Assembly<Nation> {
    private final EMCMap parent;

    public Nations(EMCMap parent) {
        this.parent = parent;
        updateCache(true);
    }

    public void updateCache(Boolean force) {
        if (this.cache != null && !force) return;

        // Parse map data into usable Nation objects.
        DataParser.parseMapData(parent.getMap(), true, false);

        Map<String, Nation> nations = DataParser.nationsAsMap();
        if (!nations.isEmpty()) cache = nations;
    }
}
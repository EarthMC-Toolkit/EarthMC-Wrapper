package io.github.emcw.map;

import io.github.emcw.caching.BaseCache;
import io.github.emcw.core.EMCMap;
import io.github.emcw.objects.Town;
import io.github.emcw.utils.DataParser;

import java.time.Duration;
import java.util.Map;

public class Towns extends BaseCache<Town> {
    private final EMCMap parent;

    public Towns(EMCMap parent) {
        super(Duration.ofMinutes(3), 0);

        this.parent = parent;
        updateCache(false);
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        if (cache != null && !force) return;

        // Parse map data into usable Town objects.
        DataParser.parseMapData(parent.getMap(), false, true);

        Map<String, Town> towns = DataParser.townsAsMap();
        if (!towns.isEmpty()) cache.putAll(towns);
    }
}
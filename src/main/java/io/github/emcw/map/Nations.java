package io.github.emcw.map;

import io.github.emcw.caching.BaseCache;
import io.github.emcw.core.EMCMap;
import io.github.emcw.objects.Nation;
import io.github.emcw.utils.DataParser;

import java.time.Duration;
import java.util.Map;

public class Nations extends BaseCache<Nation> {
    private final EMCMap parent;

    public Nations(EMCMap parent) {
        super(Duration.ofMinutes(3), 0);

        this.parent = parent;
        updateCache(true);
    }

    public void updateCache(Boolean force) {
        if (this.cache != null && !force) return;

        // Parse map data into usable Nation objects.
        DataParser.parseMapData(parent.getMap(), true, false);

        Map<String, Nation> nations = DataParser.nationsAsMap();
        if (!nations.isEmpty()) cache.putAll(nations);
    }
}
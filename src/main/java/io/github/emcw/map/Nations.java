package io.github.emcw.map;

import io.github.emcw.caching.BaseCache;
import io.github.emcw.core.EMCMap;
import io.github.emcw.entities.Nation;
import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.utils.DataParser;

import java.time.Duration;
import java.util.Map;

public class Nations extends BaseCache<Nation> implements ILocatable<Nation> {
    private final EMCMap parent;

    public Nations(EMCMap parent) {
        super(Duration.ofMinutes(3));
        this.parent = parent;
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        if (this.cache != null && !force) return;

        // Parse map data into usable Nation objects.
        DataParser.parseMapData(parent.getMap(), true, true, false);

        Map<String, Nation> nations = DataParser.parsedNations();
        if (!nations.isEmpty()) cache.putAll(nations);
    }
}
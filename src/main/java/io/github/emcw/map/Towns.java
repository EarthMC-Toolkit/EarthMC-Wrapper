package io.github.emcw.map;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.emcw.caching.BaseCache;
import io.github.emcw.core.EMCMap;
import io.github.emcw.entities.Town;
import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.utils.DataParser;

import java.time.Duration;

public class Towns extends BaseCache<Town> implements ILocatable<Town> {
    private final EMCMap parent;

    public Towns(EMCMap parent) {
        super(Duration.ofMinutes(3));
        this.parent = parent;
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        if (cache != null && !force) return;

        // Parse map data into usable Town objects.
        DataParser.parseMapData(parent.getMap(), true, false, true);

        Cache<String, Town> towns = DataParser.parsedTowns();
        if (!towns.asMap().isEmpty()) cache = towns;
    }
}
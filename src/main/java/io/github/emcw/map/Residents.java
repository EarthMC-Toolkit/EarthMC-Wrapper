package io.github.emcw.map;

import io.github.emcw.caching.BaseCache;
import io.github.emcw.core.EMCMap;
import io.github.emcw.objects.Resident;
import io.github.emcw.utils.DataParser;

import java.time.Duration;
import java.util.Map;

public class Residents extends BaseCache<Resident> {
    private final EMCMap parent;

    public Residents(EMCMap parent) {
        super(Duration.ofMinutes(3), 0);

        this.parent = parent;
        updateCache(true);
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        if (cache != null && !force) return;

        // Parse player data into usable Player objects.
        DataParser.parseMapData(parent.getMap(), false, true);

        Map<String, Resident> residents = DataParser.residentsAsMap();
        if (!residents.isEmpty()) cache.putAll(residents);
    }
}
package io.github.emcw.map;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.emcw.caching.BaseCache;
import io.github.emcw.core.EMCMap;
import io.github.emcw.entities.Resident;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.utils.DataParser;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Map;

public class Residents extends BaseCache<Resident> {
    private final EMCMap parent;

    public Residents(EMCMap parent) {
        super(Duration.ofMinutes(3));
        this.parent = parent;
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        if (!cache.asMap().isEmpty() && !force) return;

        // Parse player data into usable Player objects.
        DataParser.parseMapData(parent.getMap(), false, false, true);
        Cache<String, Resident> residents = DataParser.parsedResidents();

        // Make sure we're using valid data
        if (!residents.asMap().isEmpty())
            cache = residents;
    }

    @Override
    public Map<String, Resident> all() {
        updateCache();
        return super.all();
    }

    @Override
    public Resident single(String name) throws MissingEntryException {
        updateCache();
        return super.single(name);
    }

    public Map<String, Resident> get(String @NotNull ... keys) {
        updateCache();
        return super.get(keys);
    }
}
package io.github.emcw.map;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.emcw.caching.BaseCache;
import io.github.emcw.core.EMCMap;
import io.github.emcw.entities.Nation;
import io.github.emcw.entities.Town;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.utils.DataParser;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Map;

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
        if (!cache.asMap().isEmpty() && !force) return;

        // Parse map data into usable Town objects.
        DataParser.parseMapData(parent.getMap(), true, false, true);
        Cache<String, Town> towns = DataParser.parsedTowns();

        // Make sure were using valid data.
        if (!towns.asMap().isEmpty())
            cache = towns;
    }

    @Override
    public Map<String, Town> all() {
        updateCache();
        return super.all();
    }

    @Override
    public Town single(String name) throws MissingEntryException {
        updateCache();
        return super.single(name);
    }

    public Map<String, Town> get(String @NotNull ... keys) {
        updateCache();
        return super.get(keys);
    }
}
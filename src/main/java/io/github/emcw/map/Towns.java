package io.github.emcw.map;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;
import io.github.emcw.core.EMCMap;
import io.github.emcw.entities.Town;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.utils.DataParser;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Towns extends BaseCache<Town> implements ILocatable<Town> {
    private final EMCMap parent;

    public Towns(EMCMap parent, CacheOptions options) {
        super(options);
        this.parent = parent;

        setUpdater(this::forceUpdate);
        build();
    }

    public void tryUpdate() {
        updateCache(false);
    }

    public void forceUpdate() {
        updateCache(true);
    }

    private void updateCache(Boolean force) {
        if (!empty() && !force) return;

        // Parse map data into usable Town objects.
        DataParser.parseMapData(parent.getMapName(), true, false, true);
        Cache<String, Town> towns = DataParser.parsedTowns();

        // Make sure were using valid data.
        if (!towns.asMap().isEmpty())
            setCache(towns);
    }

    @Override
    public Map<String, Town> all() {
        tryUpdate();
        return super.all();
    }

    @Override
    public Town single(String name) throws MissingEntryException {
        tryUpdate();
        return super.single(name);
    }

    public Map<String, Town> get(String @NotNull ... keys) {
        tryUpdate();
        return super.get(keys);
    }
}
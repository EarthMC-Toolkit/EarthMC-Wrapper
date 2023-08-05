package io.github.emcw.map;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;
import io.github.emcw.caching.CacheStrategy;
import io.github.emcw.core.EMCMap;
import io.github.emcw.entities.Resident;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.utils.DataParser;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Residents extends BaseCache<Resident> {
    private final EMCMap parent;

    public Residents(EMCMap parent, CacheOptions options) {
        super(options);
        this.parent = parent;

        setUpdater(this::forceUpdate);
        forceUpdate();

        build();
    }

    public void tryUpdate() {
        tryExpire();
        updateCache(false);
    }

    public void forceUpdate() {
        updateCache(true);
    }

    private void updateCache(Boolean force) {
        if (!empty() && !force) return;

        // Parse player data into usable Player objects.
        DataParser.parseMapData(parent.getMapName(), false, false, true);
        Cache<String, Resident> residents = DataParser.parsedResidents();

        // Make sure we're using valid data
        if (!residents.asMap().isEmpty())
            setCache(residents);
    }

    @Override
    public Map<String, Resident> all() {
        tryUpdate();
        return super.all();
    }

    @Override
    public Resident single(String name) throws MissingEntryException {
        tryUpdate();
        return super.single(name);
    }

    public Map<String, Resident> get(String @NotNull ... keys) {
        tryUpdate();
        return super.get(keys);
    }
}
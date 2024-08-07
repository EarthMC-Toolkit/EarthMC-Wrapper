package io.github.emcw.map.api;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;
import io.github.emcw.EMCMap;
import io.github.emcw.map.entities.Resident;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.utils.parsers.DynmapParser;
import io.github.emcw.utils.parsers.SquaremapParser;
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
        SquaremapParser.parseMapData(false, false, true);
        Cache<String, Resident> residents = SquaremapParser.parsedResidents();

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
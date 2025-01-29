package io.github.emcw.squaremap.api;

import com.github.benmanes.caffeine.cache.Cache;

import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;
import io.github.emcw.squaremap.entities.SquaremapTown;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.squaremap.SquaremapParser;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Towns extends BaseCache<SquaremapTown> implements ILocatable<SquaremapTown> {
    SquaremapParser parser;

    public Towns(SquaremapParser parser, CacheOptions options) {
        super(options);
        this.parser = parser;

        setUpdater(this::forceUpdateCache); // Cache will be force updated each time update.run() is called.
        buildCache();
    }

    @Override
    protected void updateCache(Boolean force) {
        if (!cacheIsEmpty() && !force) return;

        // Parse map data into usable Town objects.
        parser.parseMapData(true, false, true);
        Cache<String, SquaremapTown> towns = parser.getTowns();

        // Make sure we're using valid data to populate the cache with.
        if (towns == null) return;
        if (towns.asMap().isEmpty()) return;

        // TODO: Remove logging when done
        System.out.print(towns);

        setCache(towns);
    }

    public Map<String, SquaremapTown> all() {
        tryUpdateCache();
        return super.all();
    }

    @Override
    public SquaremapTown single(String name) throws MissingEntryException {
        tryUpdateCache();
        return super.single(name);
    }

    public Map<String, SquaremapTown> get(String @NotNull ... keys) {
        tryUpdateCache();
        return super.get(keys);
    }
}
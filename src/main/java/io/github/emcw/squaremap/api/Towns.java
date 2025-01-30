package io.github.emcw.squaremap.api;

import com.github.benmanes.caffeine.cache.Cache;

import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;
import io.github.emcw.squaremap.entities.SquaremapTown;

import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.squaremap.SquaremapParser;

public class Towns extends BaseCache<SquaremapTown> implements ILocatable<SquaremapTown> {
    SquaremapParser parser;

    public Towns(SquaremapParser parser, CacheOptions options) {
        super(options);
        this.parser = parser;

        buildCache();
    }

    @Override
    protected void updateCache(Boolean force) {
        if (!cacheIsEmpty() && !force) return;

        // Parse map data into usable Town objects.
        this.parser.parseMapData(true, false, true);
        Cache<String, SquaremapTown> towns = this.parser.getTowns();

        // Make sure we're using valid data to populate the cache with.
        if (towns == null) return;
        if (towns.asMap().isEmpty()) return;

        setCache(towns);
    }
}
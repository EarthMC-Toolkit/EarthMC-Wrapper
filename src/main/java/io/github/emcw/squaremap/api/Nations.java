package io.github.emcw.squaremap.api;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;

import io.github.emcw.squaremap.SquaremapParser;
import io.github.emcw.squaremap.entities.SquaremapNation;

import io.github.emcw.interfaces.ILocatable;

public class Nations extends BaseCache<SquaremapNation> implements ILocatable<SquaremapNation> {
    SquaremapParser parser;

    public Nations(SquaremapParser parser, CacheOptions options) {
        super(options);
        this.parser = parser;

        buildCache();
    }

    @Override
    protected void updateCache(Boolean force) {
        if (!cacheIsEmpty() && !force) return;

        // Parse map data into usable Nation objects.
        this.parser.parseMapData(true, true, false);
        Cache<String, SquaremapNation> nations = this.parser.getNations();

        // Make sure we're using valid data to populate the cache with.
        if (nations == null) return;
        if (nations.asMap().isEmpty()) return;

        setCache(nations);
    }
}
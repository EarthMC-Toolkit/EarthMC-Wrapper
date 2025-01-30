package io.github.emcw.squaremap.api;

import com.github.benmanes.caffeine.cache.Cache;

import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;
import io.github.emcw.squaremap.entities.SquaremapResident;

import io.github.emcw.squaremap.SquaremapParser;

public class Residents extends BaseCache<SquaremapResident> {
    SquaremapParser parser;

    public Residents(SquaremapParser parser, CacheOptions options) {
        super(options);
        this.parser = parser;

        //forceUpdateCache(); // TODO: Investigate if pre-populating is necessary? I forgor lmao.

        buildCache();
    }

    @Override
    protected void updateCache(Boolean force) {
        if (!cacheIsEmpty() && !force) return;

        // Parse player data into usable Player objects.
        parser.parseMapData(false, false, true);
        Cache<String, SquaremapResident> residents = parser.getResidents();

        // Make sure we're using valid data to populate the cache with.
        if (residents == null) return;
        if (residents.asMap().isEmpty()) return;

        setCache(residents);
    }
}
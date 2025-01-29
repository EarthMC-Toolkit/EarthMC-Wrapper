package io.github.emcw.squaremap.api;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;

import io.github.emcw.squaremap.SquaremapParser;
import io.github.emcw.squaremap.entities.SquaremapNation;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.interfaces.ILocatable;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Nations extends BaseCache<SquaremapNation> implements ILocatable<SquaremapNation> {
    SquaremapParser parser;

    public Nations(SquaremapParser parser, CacheOptions options) {
        super(options);
        this.parser = parser;

        setUpdater(this::forceUpdateCache); // Cache will be force updated each time update.run() is called.
        buildCache();
    }

    public void tryUpdateCache() {
        tryExpireCache();
        updateCache(false);
    }

    public void forceUpdateCache() {
        updateCache(true);
    }

    void updateCache(Boolean force) {
        if (!cacheIsEmpty() && !force) return;

        // Parse map data into usable Nation objects.
        parser.parseMapData(true, true, false);
        Cache<String, SquaremapNation> nations = parser.getNations();

        // Make sure we're using valid data to populate the cache with.
        if (nations == null) return;
        if (nations.asMap().isEmpty()) return;

        setCache(nations);
    }

    @Override
    public Map<String, SquaremapNation> all() {
        tryUpdateCache();
        return super.all();
    }

    @Override
    public SquaremapNation single(String name) throws MissingEntryException {
        tryUpdateCache();
        return super.single(name);
    }

    @Override
    public Map<String, SquaremapNation> get(String @NotNull ... keys) {
        tryUpdateCache();
        return super.get(keys);
    }
}
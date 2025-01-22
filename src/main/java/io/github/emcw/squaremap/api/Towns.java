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

        setUpdater(this::forceUpdate);
        buildCache();
    }

    public void tryUpdate() {
        tryExpire();
        updateCache(false);
    }

    public void forceUpdate() {
        updateCache(true);
    }

    void updateCache(Boolean force) {
        if (!empty() && !force) return;

        // Parse map data into usable Town objects.
        parser.parseMapData(true, false, true);
        Cache<String, SquaremapTown> towns = parser.getTowns();

        // Make sure we're using valid data to populate the cache with.
        if (towns == null) return;
        if (towns.asMap().isEmpty()) return;

        System.out.print(towns);

        setCache(towns);
    }

    public Map<String, SquaremapTown> all() {
        tryUpdate();
        return super.all();
    }

    @Override
    public SquaremapTown single(String name) throws MissingEntryException {
        tryUpdate();
        return super.single(name);
    }

    public Map<String, SquaremapTown> get(String @NotNull ... keys) {
        tryUpdate();
        return super.get(keys);
    }
}
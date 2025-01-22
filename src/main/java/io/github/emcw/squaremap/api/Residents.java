package io.github.emcw.squaremap.api;

import com.github.benmanes.caffeine.cache.Cache;

import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;
import io.github.emcw.squaremap.entities.SquaremapResident;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.squaremap.SquaremapParser;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Residents extends BaseCache<SquaremapResident> {
    SquaremapParser parser;

    public Residents(SquaremapParser parser, CacheOptions options) {
        super(options);
        this.parser = parser;

        setUpdater(this::forceUpdate);
        forceUpdate();

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

        // Parse player data into usable Player objects.
        parser.parseMapData(false, false, true);
        Cache<String, SquaremapResident> residents = parser.getResidents();

        // Make sure we're using valid data to populate the cache with.
        if (residents == null) return;
        if (residents.asMap().isEmpty()) return;

        setCache(residents);
    }

    @Override
    public Map<String, SquaremapResident> all() {
        tryUpdate();
        return super.all();
    }

    @Override
    public SquaremapResident single(String name) throws MissingEntryException {
        tryUpdate();
        return super.single(name);
    }

    public Map<String, SquaremapResident> get(String @NotNull ... keys) {
        tryUpdate();
        return super.get(keys);
    }
}
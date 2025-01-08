package io.github.emcw.map.api;

import com.github.benmanes.caffeine.cache.Cache;

import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;
import io.github.emcw.map.entities.Town;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.utils.parsers.SquaremapParser;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Towns extends BaseCache<Town> implements ILocatable<Town> {

    public Towns(CacheOptions options) {
        super(options);

        setUpdater(this::forceUpdate);
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

        // Parse map data into usable Town objects.
        SquaremapParser.parseMapData(true, false, true);
        Cache<String, Town> towns = SquaremapParser.parsedTowns();

        // Make sure were using valid data.
        if (towns != null && !towns.asMap().isEmpty())
            setCache(towns);
    }

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
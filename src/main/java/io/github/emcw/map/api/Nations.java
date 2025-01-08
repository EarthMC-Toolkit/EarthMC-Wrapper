package io.github.emcw.map.api;

import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;

import io.github.emcw.map.entities.Nation;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.interfaces.ILocatable;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Nations extends BaseCache<Nation> implements ILocatable<Nation> {

    public Nations(CacheOptions options) {
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
//        if (!empty() && !force) return;
//
//        // Parse map data into usable Nation objects.
//        SquaremapParser.parseMapData(parent.getMapName(), true, true, false);
//        Cache<String, Nation> nations = SquaremapParser.parsedNations(parent.getMapName());
//
//        if (!nations.asMap().isEmpty())
//            setCache(nations);
    }

    public Map<String, Nation> all() {
        tryUpdate();
        return super.all();
    }

    @Override
    public Nation single(String name) throws MissingEntryException {
        tryUpdate();
        return super.single(name);
    }

    public Map<String, Nation> get(String @NotNull ... keys) {
        tryUpdate();
        return super.get(keys);
    }
}
package io.github.emcw.map;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;
import io.github.emcw.core.EMCMap;
import io.github.emcw.entities.Nation;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.utils.DataParser;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Nations extends BaseCache<Nation> implements ILocatable<Nation> {
    private final EMCMap parent;

    public Nations(EMCMap parent, CacheOptions options) {
        super(options);
        this.parent = parent;

        setUpdater(this::forceUpdate);
        build();
    }

    public void tryUpdate() {
        updateCache(false);
    }

    public void forceUpdate() {
        updateCache(true);
    }

    private void updateCache(Boolean force) {
        if (!empty() && !force) return;

        // Parse map data into usable Nation objects.
        DataParser.parseMapData(parent.getMapName(), true, true, false);
        Cache<String, Nation> nations = DataParser.parsedNations();

        if (!nations.asMap().isEmpty())
            setCache(nations);
    }

    @Override
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
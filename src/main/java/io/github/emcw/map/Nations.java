package io.github.emcw.map;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.emcw.caching.BaseCache;
import io.github.emcw.core.EMCMap;
import io.github.emcw.entities.Nation;
import io.github.emcw.entities.Resident;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.utils.DataParser;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Map;

public class Nations extends BaseCache<Nation> implements ILocatable<Nation> {
    private final EMCMap parent;

    public Nations(EMCMap parent) {
        super(Duration.ofMinutes(3));
        this.parent = parent;
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        if (!cache.asMap().isEmpty() && !force) return;

        // Parse map data into usable Nation objects.
        DataParser.parseMapData(parent.getMap(), true, true, false);
        Cache<String, Nation> nations = DataParser.parsedNations();

        if (!nations.asMap().isEmpty())
            cache = nations;
    }

    @Override
    public Map<String, Nation> all() {
        updateCache();
        return super.all();
    }

    @Override
    public Nation single(String name) throws MissingEntryException {
        updateCache();
        return super.single(name);
    }

    public Map<String, Nation> get(String @NotNull ... keys) {
        updateCache();
        return super.get(keys);
    }
}
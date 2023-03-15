package io.github.emcw.classes;

import io.github.emcw.core.EMCMap;
import io.github.emcw.interfaces.IMap;
import io.github.emcw.objects.Nation;
import io.github.emcw.utils.DataParser;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Nations implements IMap<Nation> {
    private final EMCMap parent;
    Map<String, Nation> cache = null;

    public Nations(EMCMap parent) {
        this.parent = parent;
        updateCache(true);
    }

    @Nullable
    public Nation single(String nationName) {
        return single(nationName, this.cache);
    }

    public Map<String, Nation> all() {
        updateCache(false);
        return this.cache;
    }

    public void updateCache(Boolean force) {
        if (this.cache != null && !force) return;

        // Parse map data into usable Nation objects.
        DataParser.parseMapData(parent.getMap(), true, false);
        this.cache = DataParser.nationsAsMap(DataParser.getNations());

        System.out.println(cache.size());
    }
}
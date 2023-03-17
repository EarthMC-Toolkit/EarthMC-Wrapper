package io.github.emcw.data;

import io.github.emcw.core.EMCMap;
import io.github.emcw.objects.Nation;
import io.github.emcw.utils.DataParser;

public class Nations extends Assembly<Nation> {
    private final EMCMap parent;

    public Nations(EMCMap parent) {
        this.parent = parent;
        updateCache(true);
    }

    public void updateCache(Boolean force) {
        if (this.cache != null && !force) return;

        // Parse map data into usable Nation objects.
        DataParser.parseMapData(parent.getMap(), true, false);
        this.cache = DataParser.nationsAsMap(DataParser.getNations());
    }
}
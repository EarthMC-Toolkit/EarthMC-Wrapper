package io.github.emcw.data;

import io.github.emcw.core.EMCMap;
import io.github.emcw.objects.Resident;
import io.github.emcw.utils.DataParser;

public class Residents extends Assembly<Resident> {
    private final EMCMap parent;

    public Residents(EMCMap parent) {
        this.parent = parent;
        updateCache(true);
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        if (cache != null && !force) return;

        // Parse player data into usable Player objects.
        DataParser.parseMapData(parent.getMap(), false, true);
        cache = DataParser.residentsAsMap(DataParser.getResidents());
    }
}
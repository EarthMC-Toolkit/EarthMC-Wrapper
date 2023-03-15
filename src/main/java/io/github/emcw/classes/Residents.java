package io.github.emcw.classes;

import io.github.emcw.core.EMCMap;
import io.github.emcw.interfaces.IMap;
import io.github.emcw.objects.Resident;
import io.github.emcw.utils.DataParser;

import java.util.Map;

public class Residents implements IMap<Resident> {
    private final EMCMap parent;
    protected Map<String, Resident> cache = null;

    public Residents(EMCMap parent) {
        this.parent = parent;
        updateCache(true);
    }

    public Resident single(String playerName) {
        updateCache();
        return single(playerName, cache);
    }

    public Map<String, Resident> all() {
        updateCache();
        return cache;
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        if (cache != null && !force) return;

        // Parse player data into usable Player objects.
        DataParser.parseMapData(parent.getMap(), false, true);
        this.cache = DataParser.residentsAsMap(DataParser.getResidents());
    }
}

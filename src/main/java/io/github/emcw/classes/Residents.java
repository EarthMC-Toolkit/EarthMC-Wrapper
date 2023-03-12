package io.github.emcw.classes;

import com.google.gson.JsonObject;
import io.github.emcw.core.EMCMap;
import io.github.emcw.interfaces.Collective;
import io.github.emcw.objects.Resident;
import io.github.emcw.utils.DataParser;
import lombok.Getter;

import java.util.List;
import java.util.Map;

import static io.github.emcw.utils.GsonUtil.keyAsStr;

public class Residents implements Collective<Resident> {
    private EMCMap parent;

    @Getter
    protected Map<String, Resident> cache;

    public Residents(EMCMap parent) {
        this.parent = parent;
        updateCache(true);
    }

    public Resident single(JsonObject p) {
        String name = keyAsStr(p, "name");
        return single(name);
    }

    public Resident single(String playerName) {
        updateCache();
        return Collective.super.single(playerName, getCache());
    }

    public List<Resident> all() {
        updateCache();
        return Collective.super.all(getCache());
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        if (getCache() != null && !force) return;

        // Parse player data into usable Player objects.
        DataParser.parseMapData(parent.getMap(), false);
        this.cache = DataParser.residentsAsMap(DataParser.getResidents());
    }
}

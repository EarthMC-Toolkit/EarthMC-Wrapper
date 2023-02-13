package net.emc.emcw.classes;

import com.google.gson.JsonObject;
import net.emc.emcw.objects.Town;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Towns {
    Map<String, Town> cache;

    public Town single(String name) {
        return this.cache.get(name);
    }

    public List<Town> all() {
        return new ArrayList<>(this.cache.values());
    }

    void updateCache(JsonObject data) {
        // Send req to /marker_earth.json

        // Strip html and parse elements as Town object

        // Push town objects to cache
        Town t = new Town();
        this.cache.put("test", t);
    }
}

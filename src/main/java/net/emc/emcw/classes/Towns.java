package net.emc.emcw.classes;

import com.google.gson.JsonObject;
import net.emc.emcw.objects.Town;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Towns {
    static Map<String, Town> cache;

    public static Town single(String name) {
        return cache.get(name);
    }

    public List<Town> all() {
        return new ArrayList<>(cache.values());
    }

    void updateCache(JsonObject data) {
        // Send req to /marker_earth.json

        // Strip html and parse elements as Town object

        // Push town objects to cache
        Town t = new Town();
        cache.put("test", t);
    }
}

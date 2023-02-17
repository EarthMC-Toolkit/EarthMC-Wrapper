package net.emc.emcw.classes;

import net.emc.emcw.objects.Nation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Nations {
    static Map<String, Nation> cache;

    public static Nation single(String name) {
        return cache.get(name);
    }

    public List<Nation> all() {
        return new ArrayList<>(cache.values());
    }

}
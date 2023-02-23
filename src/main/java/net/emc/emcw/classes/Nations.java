package net.emc.emcw.classes;

import com.google.gson.JsonArray;
import net.emc.emcw.interfaces.Collective;
import net.emc.emcw.objects.Nation;
import net.emc.emcw.objects.Town;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Nations implements Collective<Nation> {
    Map<String, Nation> cache = null;
    String map;

    public Nations(String mapName) {
        this.map = mapName;
    }

    public Nation single(String key) throws NullPointerException {
        return Collective.super.single(key, this.cache);
    }

    static List<Nation> fromArray(JsonArray arr) {
        return arr.asList().stream()
                .map(p -> new Nation(p.getAsJsonObject()))
                .collect(Collectors.toList());
    }

    public void updateCache(Map<String, Town> towns) {
        Map<String, Nation> nationList = new HashMap<>();

        this.cache = nationList;
    }
}
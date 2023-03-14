package io.github.emcw.interfaces;

import java.util.*;

import static io.github.emcw.utils.GsonUtil.streamEntries;

public interface Collective<T> {
    default T single(String key, Map<String, T> map) throws NullPointerException {
        return map.getOrDefault(key, null);
    }

    default T merge(Map<String, T> map1, Map<String, T> map2) {
        // In parallel
        streamEntries(map2).forEach(e -> {

        });

        // Loop through map2
        // Exists in map1, merge.
        // Doesn't exist in map1, put.

        return null;
    }
}
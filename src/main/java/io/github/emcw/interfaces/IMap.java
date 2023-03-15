package io.github.emcw.interfaces;

import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.utils.Funcs;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.emcw.utils.Funcs.collectAsMap;
import static io.github.emcw.utils.GsonUtil.streamEntries;

public interface IMap<T> {
    default T single(String key, Map<String, T> map) {
        return map.getOrDefault(key, null);
    }

    default Map<String, T> get(@NotNull Map<String, T> map, String[] keys) throws MissingEntryException {
        if (map.isEmpty()) throw new MissingEntryException();
        Map<String, T> result = new ConcurrentHashMap<>();

        return collectAsMap(streamEntries(map).filter(entry -> Funcs.arrayHas(keys, entry.getKey())));
    }
}
package io.github.emcw.interfaces;

import io.github.emcw.exceptions.MissingEntryException;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static io.github.emcw.utils.Funcs.arrayHas;
import static io.github.emcw.utils.Funcs.collectAsMap;
import static io.github.emcw.utils.GsonUtil.streamEntries;

public interface IMap<T> {
    default T single(String key, Map<String, T> map) {
        return map.getOrDefault(key, null);
    }

    default Map<String, T> get(@NotNull Map<String, T> map, String... keys) throws MissingEntryException {
        if (map.isEmpty()) throw new MissingEntryException();
        return collectAsMap(streamEntries(map).filter(entry -> arrayHas(keys, entry.getKey())));
    }
}
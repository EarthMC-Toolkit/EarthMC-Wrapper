package io.github.emcw.data;

import io.github.emcw.exceptions.MissingEntryException;

import static io.github.emcw.utils.Funcs.arrayHas;
import static io.github.emcw.utils.Funcs.collectAsMap;
import static io.github.emcw.utils.GsonUtil.streamEntries;

import java.util.*;

public abstract class Assembly<T> {
    protected Map<String, T> cache = null;

    public Map<String, T> get(String... keys) throws MissingEntryException {
        if (cache.isEmpty()) throw new MissingEntryException();
        return collectAsMap(streamEntries(cache).filter(entry -> arrayHas(keys, entry.getKey())));
    }

    public T single(String key) {
        return cache.getOrDefault(key, null);
    }

    public Map<String, T> all() {
        return cache;
    }
}
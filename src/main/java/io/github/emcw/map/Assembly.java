package io.github.emcw.map;

import java.util.Map;

import io.github.emcw.exceptions.MissingEntryException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import static io.github.emcw.utils.Funcs.arrayHas;
import static io.github.emcw.utils.Funcs.collectAsMap;
import static io.github.emcw.utils.GsonUtil.streamEntries;
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class Assembly<T> {
    protected Map<String, T> cache = null;

    public Map<String, T> get(String... keys) throws MissingEntryException {
        if (cache.isEmpty()) throw new MissingEntryException();
        return collectAsMap(streamEntries(cache).filter(entry -> arrayHas(keys, entry.getKey())));
    }

    @Nullable
    public T single(String key) {
        return cache.getOrDefault(key, null);
    }

    public Map<String, T> all() {
        return cache;
    }
}
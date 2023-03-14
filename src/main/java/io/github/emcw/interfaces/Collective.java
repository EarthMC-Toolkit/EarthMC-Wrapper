package io.github.emcw.interfaces;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public interface Collective<T> {
    default T single(String key, @NotNull Map<String, T> map) throws NullPointerException {
        return map.getOrDefault(key, null);
    }
}
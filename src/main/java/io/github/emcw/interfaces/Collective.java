package io.github.emcw.interfaces;

import java.util.*;

public interface Collective<T> {
    default T single(String key, Map<String, T> map) throws NullPointerException {
        return map.getOrDefault(key, null);
    }
}
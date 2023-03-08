package io.github.emcw.interfaces;

import io.github.emcw.utils.Funcs;

import java.util.*;

public interface Collective<T> {
    @SuppressWarnings("unchecked")
    default T single(String key, Map<String, T> arr) throws NullPointerException {
        try { return arr.get(key); }
        catch(NullPointerException e) {
            return null;
        }
    }

    default List<T> all(Map<String, T> f) {
        return Funcs.mapToList(f);
    }
}
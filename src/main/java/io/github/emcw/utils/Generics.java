package io.github.emcw.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Generics {
    public static <K, V> List<V> mapToList(Map<K, V> map) {
        return new ArrayList<>(map.values());
    }
}

package net.emc.emcw.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Generics {
    public static <K, V> List<V> mapToList(Map<K, V> map) {
        return map.values().stream().distinct().collect(Collectors.toList());
    }
}

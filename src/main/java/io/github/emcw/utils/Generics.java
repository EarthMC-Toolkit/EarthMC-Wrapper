package io.github.emcw.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Generics {
    public static <K, V> List<V> mapToList(Map<K, V> map) {
        return new ArrayList<>(map.values());
    }

    public static Stream<Map.Entry<String, JsonElement>> streamEntries(JsonObject o) {
        return o.entrySet().parallelStream();
    }

    static <T> Map<String, T> collectAsMap(Stream<Map.Entry<String, T>> stream) {
        return stream.filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

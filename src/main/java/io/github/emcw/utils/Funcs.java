package io.github.emcw.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.emcw.objects.Base;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Funcs {
    public static <T> Map<String, T> listToMap(List<Base<T>> list, String key) {
        ConcurrentHashMap<String, T> map = new ConcurrentHashMap<>();
        list.parallelStream().forEach(el -> map.put(el.getName(), el.getValue()));
        return map;
    }

    public static <K, V> List<V> mapToList(Map<K, V> map) {
        return new ArrayList<>(map.values());
    }

    public static Stream<Map.Entry<String, JsonElement>> streamEntries(JsonObject o) {
        return o.entrySet().parallelStream();
    }

    static <T> Map<String, T> collectAsMap(@NotNull Stream<Map.Entry<String, T>> stream) {
        return stream.filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static int calcArea(int[] X, int[] Z) {
        return calcArea(X, Z, X.length, 256);
    }

    public static int calcArea(int[] X, int[] Z, int numPoints, int divisor) {
        return Math.abs(IntStream.range(0, numPoints).parallel().map(i -> {
            int j = (i + numPoints - 1) % numPoints;
            return (X[j] + X[i]) * (Z[j] - Z[i]);
        }).sum() / 2) / divisor;
    }

    public static Integer range(int[] args) {
        IntSummaryStatistics stat = Arrays.stream(args).parallel().summaryStatistics();
        return Math.round((stat.getMin() + stat.getMax()) / 2.0f);
    }
}

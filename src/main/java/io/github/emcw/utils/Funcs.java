package io.github.emcw.utils;

import io.github.emcw.objects.Base;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Funcs {
    public static <T> Map<String, T> listToMap(List<Base<T>> list) {
        ConcurrentHashMap<String, T> map = new ConcurrentHashMap<>();
        list.parallelStream().forEach(el -> map.put(el.getName(), el.getParent()));
        return map;
    }

    public static <K, V> List<V> mapToList(Map<K, V> map) {
        return new ArrayList<>(map.values());
    }

    public static <T> Map<String, T> collectAsMap(@NotNull Stream<Map.Entry<String, T>> stream) {
        return stream.filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static boolean arrayHas(String[] strings, String str) {
       return Arrays.stream(strings).parallel().anyMatch(str::equals);
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
        return Math.round((stat.getMin() + stat.getMax()) / 2f);
    }

    public static <T> List<T> removeListDuplicates(List<T> list) {
        return list.parallelStream().distinct().collect(Collectors.toList());
    }

//    public static boolean sqr(Player a, Player b, double range) {
//        double distance = Math.hypot(a.getX() - b.getX(), a.getZ() - b.getZ());
//        return distance <= range;
//    }

    public static <T> boolean sqr(T a, T b, double range,
                                  Function<T, Double> xGetter,
                                  Function<T, Double> zGetter) {
        double distance = Math.hypot(xGetter.apply(a) - xGetter.apply(b),
                zGetter.apply(a) - zGetter.apply(b));
        return distance <= range;
    }

    public static boolean hypot(double num, double[] args) {
        double input = args[0], radius = args[1];
        return (num <= input+radius) && (num >= input-radius);
    }
}

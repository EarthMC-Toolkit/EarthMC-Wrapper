package io.github.emcw.utils;

import io.github.emcw.entities.BaseEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Funcs {
    public static <T> @NotNull Map<String, T> listToMap(@NotNull List<BaseEntity<T>> list) {
        ConcurrentHashMap<String, T> map = new ConcurrentHashMap<>();
        list.parallelStream().forEach(el -> map.put(el.getName(), el.getParent()));
        return map;
    }

    @Contract("_ -> new")
    public static <K, V> @NotNull List<V> mapToList(@NotNull Map<K, V> map) {
        return new ArrayList<>(map.values());
    }

    public static <T> Map<String, T> collectAsMap(@NotNull Stream<Map.Entry<String, T>> stream) {
        return stream.filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static boolean arrayHas(String[] strings, @NotNull String str) {
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

    public static @NotNull Integer range(int[] args) {
        IntSummaryStatistics stat = Arrays.stream(args).parallel().summaryStatistics();
        return Math.round((stat.getMin() + stat.getMax()) / 2f);
    }

    public static <T> List<T> removeListDuplicates(@NotNull List<T> list) {
        return list.parallelStream().distinct().collect(Collectors.toList());
    }

    public static Integer euclidean(int x1, int x2, int z1, int z2) {
        return (int) Math.hypot(x1 - x2, z1 - z2);
    }

    public static @NotNull Integer manhattan(int x1, int x2, int z1, int z2) {
        return Math.abs(x1 - x2) + Math.abs(z1 - z2);
    }

    @Contract(pure = true)
    public static <T> Stream<T> streamList(@NotNull List<T> list) {
        return list.parallelStream();
    }

    @Contract(pure = true)
    public static boolean withinRadius(Integer num, Integer @NotNull [] args) {
        Integer input = args[0], radius = args[1];
        return (num <= input+radius) && (num >= input-radius);
    }

    public static boolean withinRadius(Integer sourceCoord, Integer targetCoord, Integer radius) {
        return (sourceCoord <= targetCoord+radius) && (sourceCoord >= targetCoord-radius);
    }
}
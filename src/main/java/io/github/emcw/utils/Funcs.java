package io.github.emcw.utils;

import io.github.emcw.squaremap.entities.SquaremapLocation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Funcs {
//    public static <T> @NotNull Map<String, T> listToMap(@NotNull List<BaseEntity<T>> list) {
//        ConcurrentHashMap<String, T> map = new ConcurrentHashMap<>();
//        list.parallelStream().forEach(el -> map.put(el.getName(), el.getParent()));
//
//        return map;
//    }

//    @Contract("_ -> new")
//    public static <K, V> @NotNull List<V> mapToList(@NotNull Map<K, V> map) {
//        return new ArrayList<>(map.values());
//    }

    public static <T> Map<String, T> collectAsMap(@NotNull Stream<Map.Entry<String, T>> stream) {
        return stream.filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static boolean strArrHas(String[] arr, @NotNull String str) {
       return streamStrArr(arr).anyMatch(str::equals);
    }

    public static int calcArea(int[] X, int[] Z) {
        return calcArea(X, Z, X.length);
    }

    public static int calcArea(int[] X, int[] Z, int numPoints, int @NotNull ... divisor) {
        IntStream ints = IntStream.range(0, numPoints).map(i -> {
            int j = (i + numPoints - 1) % numPoints;
            return (X[j] + X[i]) * (Z[j] - Z[i]);
        });

        int sum = ints.sum() / 2;
        int div = divisor.length < 1 ? 256 : divisor[0];

        return Math.abs(sum / div);
    }

    public static int roundToNearest16(int value) {
        return Math.round((float) value / 16) * 16;
    }

    public static int roundToNearest16(double value) {
        return (int) (Math.round(value / 16) * 16);
    }

    /**
     * Applies the <a href="https://www.geeksforgeeks.org/mid-range/#mid-range-formula">Midrange Formula</a>
     * to the input array, the result is then rounded to the nearest full integer.
     * @param ints The input array of integers.
     * @return The resulting int (rounded).
     */
    public static int midrange(int[] ints) {
        IntSummaryStatistics stats = IntStream.of(ints).summaryStatistics();
        return Math.round(stats.getMax() + stats.getMin() / 2f);
    }

    public static int euclidean(int x1, int x2, int z1, int z2) {
        return (int) Math.hypot(x1 - x2, z1 - z2);
    }

    public static @NotNull Integer manhattan(@NotNull SquaremapLocation loc1, @NotNull SquaremapLocation loc2) {
        return manhattan(loc1.getX(), loc2.getX(), loc1.getZ(), loc2.getZ());
    }

    public static @NotNull Integer manhattan(int x1, int x2, int z1, int z2) {
        return Math.abs(x1 - x2) + Math.abs(z1 - z2);
    }

    public static <T> List<T> removeListDuplicates(@NotNull List<T> list) {
        return collectList(list.parallelStream(), true);
    }

    public static <T> List<T> collectList(Stream<T> stream, Boolean unique) {
        return (unique ? stream.distinct() : stream)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public static @NotNull Stream<String> streamStrArr(@NotNull String[] arr) {
        return Stream.of(arr).toList().parallelStream();
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
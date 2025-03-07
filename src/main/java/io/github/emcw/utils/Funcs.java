package io.github.emcw.utils;

import io.github.emcw.common.Point2D;
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

    /**
     * Calculates the distance between two instances of {@link Point2D} using Euclidean geometry.
     * Note that the order of parameters here does not matter, the distance will be the same.
     * <br><br>
     *
     * See the underlying euclidean method for further info.
     * @see #euclidean(int, int, int, int)
     */
    public static double euclidean(@NotNull Point2D loc1, @NotNull Point2D loc2) {
        return euclidean(
            loc1.getX(), loc1.getZ(),
            loc2.getX(), loc2.getZ()
        );
    }

    /**
     * Calculates the straight-line distance between two locations, ignoring any obstacles or other factors.
     * This allows for diagonal paths that may not be viable in reality, the result is best only in theory.
     * <br><br>
     * In Minecraft, this method is preferred in many cases over Manhattan distance. You can visit
     * <a href="https://minecraft.wiki/w/Distance">here</a> to look at examples of when to use one over the other.
     * @param x1 The X coordinate (left/right) of the first location.
     * @param z1 The Z coordinate (up/down) of the first location.
     * @param x2 The position on the X axis (left/right) of the second location.
     * @param z2 The position on the Z axis (up/down) of the second location.
     * @return The distance as a highly precise number.
     */
    public static double euclidean(int x1, int x2, int z1, int z2) {
        return Math.hypot(x1 - x2, z1 - z2);
    }

    /**
     * Calculates the distance between two instances of {@link Point2D} using Taxi-cab geometry.
     * See the underlying manhattan method for further info.
     * @see #manhattan(int, int, int, int)
     */
    public static int manhattan(@NotNull Point2D loc1, @NotNull Point2D loc2) {
        return manhattan(
            loc1.getX(), loc1.getZ(),
            loc2.getX(), loc2.getZ()
        );
    }

    /**
     * Calculates the Manhattan (sometimes referred to as "taxi-cab") distance between two locations, which is the sum of the
     * absolute differences of their X and Z coordinates.
     * <br><br>
     * This method only considers horizontal and vertical movements, not diagonal.
     * This is usually the method to use when we want the result in blocks and precision is not required.
     * @param x1 The position on the X axis (left/right) of the first location.
     * @param z1 The position on the Z axis (up/down) of the first location.
     * @param x2 The position on the X axis (left/right) of the second location.
     * @param z2 The position on the Z axis (up/down) of the second location.
     * @see <a href="https://en.wikipedia.org/wiki/Taxicab_geometry">Taxicab Geometry</a>
     */
    public static int manhattan(int x1, int z1, int x2, int z2) {
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

    public static @NotNull <T> Stream<T> parallelStreamArr(@NotNull T[] arr) {
        return Stream.of(arr).parallel();
    }

    public static boolean strArrHas(String[] arr, @NotNull String str) {
        return parallelStreamArr(arr).anyMatch(str::equals);
    }

    @Contract(pure = true)
    public static boolean withinRadius(int num, int[] args) {
        int input = args[0];
        int radius = args[1];

        return (num <= input+radius) && (num >= input-radius);
    }

    public static boolean withinRadius(Integer sourceCoord, Integer targetCoord, Integer radius) {
        return (sourceCoord <= targetCoord+radius) && (sourceCoord >= targetCoord-radius);
    }

    /**
     * Ensures a {@link UUID} is full (with hyphens) by inserting them at known points in the string.<br><br>
     * If the given string is 36 characters (full uuid), it will use that one without inserting.
     * @param uuidStr A trimmed or full UUID in string representation.
     * @return A new {@link UUID} with hyphens inserted if not already.
     */
    public static UUID stringToFullUUID(@NotNull String uuidStr) throws IllegalArgumentException {
        if (uuidStr.length() == 36) {
            return UUID.fromString(uuidStr);
        }

        StringBuilder builder = new StringBuilder(uuidStr.trim());

        try {
            /* Backwards adding to avoid index adjustments */
            builder.insert(20, "-");
            builder.insert(16, "-");
            builder.insert(12, "-");
            builder.insert(8, "-");
        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException();
        }

        return UUID.fromString(builder.toString());
    }
}
package io.github.emcw.utils;

import io.github.emcw.map.entities.BaseEntity;
import io.github.emcw.map.entities.Location;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jsoup.safety.Safelist;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Funcs {
    @Getter private static final Safelist whitelist = new Safelist().addAttributes("a", "href");

    public static <T> @NotNull Map<String, T> listToMap(@NotNull List<BaseEntity<T>> list) {
        ConcurrentHashMap<String, T> map = new ConcurrentHashMap<>();
        list.parallelStream().forEach(el -> map.put(el.getName(), el.getParent()));

        return map;
    }

    @Contract("_ -> new")
    public static <K, V> @NotNull List<V> mapToList(@NotNull Map<K, V> map) {
        return new ArrayList<>(map.values());
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> collectEntities(@NotNull Stream<? extends BaseEntity<T>> stream) {
        return (Map<String, T>) stream.filter(Objects::nonNull)
                .collect(Collectors.toMap(BaseEntity::getName, Function.identity()));
    }

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
        IntStream ints = streamIntRange(numPoints).map(i -> {
            int j = (i + numPoints - 1) % numPoints;
            return (X[j] + X[i]) * (Z[j] - Z[i]);
        });

        int sum = ints.sum() / 2,
            div = divisor.length < 1 ? 256 : divisor[0];

        return Math.abs(sum / div);
    }

    public static int roundToNearest16(int value) {
        return Math.round((float) value / 16) * 16;
    }

    public static int roundToNearest16(double value) {
        return (int) (Math.round(value / 16) * 16);
    }

    public static @NotNull Integer range(int[] nums) {
        IntSummaryStatistics stats = IntStream.of(nums).summaryStatistics();
        return Math.round((stats.getMin() + stats.getMax()) / 2f);
    }

    public static Integer euclidean(int x1, int x2, int z1, int z2) {
        return (int) Math.hypot(x1 - x2, z1 - z2);
    }

    public static @NotNull Integer manhattan(@NotNull Location loc1, @NotNull Location loc2) {
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

    public static @NotNull IntStream streamIntRange(int max, int @NotNull ... min) {
        return IntStream.range(min.length < 1 ? 0 : min[0], max).parallel();
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
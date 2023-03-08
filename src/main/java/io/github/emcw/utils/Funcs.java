package io.github.emcw.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Funcs {
    public static <K, V> List<V> mapToList(Map<K, V> map) {
        return new ArrayList<>(map.values());
    }

    public static Stream<Map.Entry<String, JsonElement>> streamEntries(JsonObject o) {
        return o.entrySet().parallelStream();
    }

    static <T> Map<String, T> collectAsMap(Stream<Map.Entry<String, T>> stream) {
        return stream.filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static int calcArea(int[] X, int[] Z, int numPoints, int divisor) {
        return Math.abs(IntStream.range(0, numPoints).parallel().map(i -> {
            int j = (i + numPoints - 1) % numPoints;
            return (X[j] + X[i]) * (Z[j] - Z[i]);
        }).sum() / 2) / divisor;
    }

//    public static double calcArea(double[] X, double[] Z, int numPoints, int divisor) {
//        int i = 0, j = numPoints - 1;
//        double area = 0;
//        for (; i < numPoints; i++) {
//            area += (X[j] + X[i]) * (Z[j] - Z[i]);
//            j = i;
//        }
//        return Math.abs(area / 2) / divisor;
//    }

    public static Integer range(int[] args) {
        IntSummaryStatistics stat = Arrays.stream(args).parallel().summaryStatistics();
        return Math.round((stat.getMin() + stat.getMax()) / 2.0f);
    }
}

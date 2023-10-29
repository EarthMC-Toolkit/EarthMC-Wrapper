package io.github.emcw.utils

import io.github.emcw.EMCMap
import io.github.emcw.EMCWrapper.Companion.instance
import io.github.emcw.map.entities.BaseEntity
import io.github.emcw.map.entities.Location
import lombok.AccessLevel
import lombok.NoArgsConstructor
import org.jetbrains.annotations.Contract
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.IntStream
import java.util.stream.Stream

@Suppress("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
object Funcs {
    fun <T> listToMap(list: List<BaseEntity<T>>): Map<String?, T?> {
        val map = ConcurrentHashMap<String?, T?>()
        list.parallelStream().forEach { el: BaseEntity<T> -> map[el.name!!] = el.parent }
        return map
    }

    @Contract("_ -> new")
    fun <K, V> mapToList(map: Map<K, V>): List<V> {
        return ArrayList(map.values)
    }

    fun <T> collectEntities(stream: Stream<out BaseEntity<T>?>): Map<String, T> {
        return stream.filter { obj: Any? -> Objects.nonNull(obj) }
            .collect(Collectors.toMap(BaseEntity::name, Function.identity())) as Map<String, T>
    }

    fun <T> collectAsMap(stream: Stream<Map.Entry<String?, T>?>): Map<String, T> {
        return stream.filter { obj: Map.Entry<String?, T>? -> Objects.nonNull(obj) }.collect(
            Collectors.toMap<Map.Entry<String?, T>?, String, T>(
                Function<Map.Entry<String?, T>?, String> { (key, value) -> java.util.Map.Entry.key },
                Function<Map.Entry<String?, T>?, T> { (key, value) -> java.util.Map.Entry.value })
        )
    }

    fun arrayHas(arr: Array<String>, str: String): Boolean {
        return GsonUtil.strArrAsStream(arr).anyMatch { anObject: String? -> str.equals(anObject) }
    }

    fun calcArea(X: IntArray?, Z: IntArray?): Int {
        return calcArea(X, Z, X!!.size)
    }

    fun calcArea(X: IntArray?, Z: IntArray?, numPoints: Int, vararg divisor: Int): Int {
        val ints = streamIntRange(numPoints).map { i: Int ->
            val j = (i + numPoints - 1) % numPoints
            (X!![j] + X[i]) * (Z!![j] - Z[i])
        }
        val sum = ints.sum() / 2
        val div = if (divisor.size < 1) 256 else divisor[0]
        return Math.abs(sum / div)
    }

    fun range(args: IntArray?): Int {
        val stat = streamInts(*args!!).summaryStatistics()
        return Math.round((stat.min + stat.max) / 2f)
    }

    fun euclidean(x1: Int, x2: Int, z1: Int, z2: Int): Int {
        return Math.hypot((x1 - x2).toDouble(), (z1 - z2).toDouble()).toInt()
    }

    fun manhattan(loc1: Location, loc2: Location): Int {
        return manhattan(loc1.x, loc2.x, loc1.z, loc2.z)
    }

    fun manhattan(x1: Int, x2: Int, z1: Int, z2: Int): Int {
        return Math.abs(x1 - x2) + Math.abs(z1 - z2)
    }

    fun <T> removeListDuplicates(list: List<T>): List<T> {
        return collectList(streamList(list), true)
    }

    fun <T> collectList(stream: Stream<T>, noDuplicates: Boolean): List<T> {
        return (if (noDuplicates) stream.distinct() else stream)
            .filter { obj: T -> Objects.nonNull(obj) }
            .collect(Collectors.toList())
    }

    @Contract(pure = true)
    fun <T> streamList(list: List<T>): Stream<T> {
        return list.parallelStream()
    }

    fun streamIntRange(max: Int, vararg min: Int): IntStream {
        return IntStream.range(if (min.size < 1) 0 else min[0], max).parallel()
    }

    fun streamInts(vararg ints: Int): IntStream {
        return IntStream.of(*ints).parallel()
    }

    fun mapInstance(name: String): EMCMap {
        val wrapper = instance()
        return if (name == "nova") wrapper.Nova else wrapper.Aurora
    }

    @Contract(pure = true)
    fun withinRadius(num: Int, args: Array<Int?>): Boolean {
        val input = args[0]
        val radius = args[1]
        return num <= input!! + radius!! && num >= input - radius
    }

    fun withinRadius(sourceCoord: Int, targetCoord: Int, radius: Int): Boolean {
        return sourceCoord <= targetCoord + radius && sourceCoord >= targetCoord - radius
    }
}
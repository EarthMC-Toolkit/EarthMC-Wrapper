package io.github.emcw.utils

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import io.github.emcw.adapters.ColorAdapter
import io.github.emcw.adapters.DurationAdapter
import io.github.emcw.map.entities.BaseEntity
import io.github.emcw.map.entities.Player
import lombok.AccessLevel
import lombok.Getter
import lombok.NoArgsConstructor
import java.awt.Color
import java.lang.reflect.Type
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream

@NoArgsConstructor(access = AccessLevel.PRIVATE)
object GsonUtil {
    @Getter
    private val GSON = GsonBuilder()
        .registerTypeAdapter(Color::class.java, ColorAdapter())
        .registerTypeAdapter(Duration::class.java, DurationAdapter())
        .setPrettyPrinting().create()

    @JvmStatic
    fun <T> serialize(obj: Any?): String {
        return GSON.toJson(obj, getType(obj))
    }

    fun <T> getType(obj: T): Type {
        return TypeToken.get(obj.javaClass).getType()
    }

    fun <T> getType(clazz: Class<T>?): Type {
        return TypeToken.getParameterized(clazz).type
    }

    fun <T> deserialize(str: String?, c: Class<T>?): T {
        return GSON.fromJson(str, c)
    }

    fun <T> deserialize(str: String?, type: Type?): T {
        return GSON.fromJson(str, type)
    }

    fun <T> deserialize(el: JsonElement?, type: Type?): T {
        return GSON.fromJson(el, type)
    }

    fun <T> convert(obj: Any?, clazz: Class<T>?): T {
        return deserialize(serialize<Any>(obj), clazz)
    }

    fun <T : JsonElement?> asTree(input: Any?): T {
        val tree: JsonElement = GsonUtil.getGSON().toJsonTree(input)
        return if (tree.isJsonObject) tree.asJsonObject as T else tree as T
    }

    fun <T> toList(obj: Any?): List<T> {
        return convert<List<*>>(obj, MutableList::class.java)
    }

    fun <T> mapToArr(map: Map<String?, T>): JsonArray {
        val arr = JsonArray()
        map.values.forEach(Consumer { v: T -> arr.add(asTree<JsonElement>(v)) })
        return arr
    }

    fun <T> arrToMap(arr: JsonArray, key: String?): Map<String, T> {
        val map = ConcurrentHashMap<String, T>()
        arrAsStream(arr).forEach { el: JsonElement ->
            val obj = el.asJsonObject
            val k = keyAsStr(obj, key) ?: return@forEach
            map[k] = deserialize(el, getType(el))
        }
        return map
    }

    fun arrToIntArr(arr: JsonArray): IntArray {
        return convert(arr, IntArray::class.java)
    }

    fun arrFromStrArr(obj: Array<String?>): JsonArray {
        val arr = JsonArray()
        for (value in obj) {
            arr.add(deserialize(value, JsonElement::class.java))
        }
        return arr
    }

    fun strArrAsStream(arr: Array<out String>): Stream<String> {
        return Stream.of(*arr).toList().parallelStream()
    }

    fun arrAsStream(arr: JsonArray): Stream<JsonElement> {
        return arr.asList().parallelStream()
    }

    fun streamEntries(o: JsonObject): Stream<Map.Entry<String?, JsonElement>> {
        return streamEntries(o.asMap())
    }

    fun <T> streamEntries(o: Map<String?, T>): Stream<Map.Entry<String?, T>> {
        return o.entries.parallelStream()
    }

    fun <T> streamValues(o: Map<String?, T>): Stream<T> {
        return o.values.parallelStream()
    }

    fun intersection(arr: JsonArray, arr2: JsonArray): Map<String?, JsonObject?> {
        return arrAsStream(arr).flatMap { obj: JsonElement? -> arrAsStream(arr2)
            .map { el: JsonElement -> el.asJsonObject }
            .filter { obj2: JsonObject? -> member(obj2, "name") == obj }
        }.collect(Collectors.toMap(
            { obj: JsonObject? -> keyAsStr(obj, "name") },
            { obj: JsonObject? -> obj })
        )
    }

    @JvmOverloads
    fun difference(ops: JsonArray?, residents: JsonArray, key: String? = "name"): Map<String, Player> {
        val names = arrAsStream(residents).filter { obj: JsonElement? -> Objects.nonNull(obj) }
            .map { res: JsonElement -> keyAsStr(res.asJsonObject, key) }
            .collect(Collectors.toSet())
        val playerListType = object : TypeToken<List<Player?>?>() {}.type
        val playerList = deserialize<List<Player>>(serialize<Any>(ops), playerListType)
        return playerList.parallelStream()
            .filter { obj: Player? -> Objects.nonNull(obj) }
            .filter { op: Player -> !names.contains(op.name) }
            .collect(Collectors.toMap(BaseEntity::name, Function.identity()))
    }

    fun <T> valueAsObj(entry: Map.Entry<String?, T>): JsonObject {
        return entryVal(entry) as JsonObject
    }

    fun <T> entryVal(entry: Map.Entry<String?, T>): T {
        return entry.value
    }

    fun member(o: JsonObject?, k: String?): JsonElement? {
        return o?.get(k)
    }

    fun isNull(el: JsonElement?): Boolean {
        return el === JsonNull.INSTANCE || el == null
    }

    fun keyAsObj(o: JsonObject?, k: String?): JsonObject? {
        val key = member(o, k)
        val result = JsonObject()
        key!!.asJsonArray.forEach(Consumer { el: JsonElement? -> result.add(k, el) })
        return if (isNull(key)) null else result
    }

    fun keyAsBool(o: JsonObject?, k: String?): Boolean {
        val key = member(o, k)
        return key != null && key.asBoolean
    }

    fun keyAsInt(o: JsonObject?, k: String?): Int? {
        val key = member(o, k)
        return if (isNull(key)) null else key!!.asInt
    }

    fun keyAsStr(o: JsonObject?, k: String?): String? {
        val key = member(o, k)
        return if (isNull(key)) null else key!!.asString
    }

    fun keyAsArr(obj: JsonObject, key: String?): JsonArray {
        var arr = JsonArray()
        try {
            arr = Objects.requireNonNull(member(obj, key)).asJsonArray
        } catch (e: IllegalStateException) {
            arr.add(obj[key])
        }
        return arr
    }

    fun mapToObj(map: Map<String?, JsonObject?>): JsonObject {
        val obj = JsonObject()
        map.forEach { (property: String?, value: JsonObject?) -> obj.add(property, value) }
        return obj
    }
}
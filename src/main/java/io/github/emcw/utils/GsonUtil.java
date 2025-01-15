package io.github.emcw.utils;

import io.github.emcw.adapters.ColorAdapter;
import io.github.emcw.adapters.DurationAdapter;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("unchecked, unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GsonUtil {
    @Getter private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(Color.class, new ColorAdapter())
        .registerTypeAdapter(Duration.class, new DurationAdapter())
        .setPrettyPrinting()
        .serializeNulls()
        .create();

    public static String serialize(Object obj) {
        return GSON.toJson(obj, getType(obj));
    }

    public static <T> Type getType(@NotNull T obj) {
        return TypeToken.get(obj.getClass()).getType();
    }

    public static <T> Type getType(Class<T> clazz) {
        return TypeToken.getParameterized(clazz).getType();
    }

    public static <T> T deserialize(String str, Class<T> c) {
        return GSON.fromJson(str, c);
    }

    public static <T> T deserialize(String str, Type type) {
        return GSON.fromJson(str, type);
    }

    public static <T> T deserialize(JsonElement el, Type type) {
        return GSON.fromJson(el, type);
    }

    public static <T> T convert(Object obj, Class<T> clazz) {
        return deserialize(serialize(obj), clazz);
    }

    public static <T extends JsonElement> T asTree(Object input) {
        JsonElement tree = getGSON().toJsonTree(input);
        return tree.isJsonObject() ? (T) tree.getAsJsonObject() : (T) tree;
    }

    public static <T> List<T> toList(Object obj) {
       return convert(obj, List.class);
    }

    public static <T> @NotNull JsonArray mapToArr(@NotNull Map<String, T> map) {
        JsonArray arr = new JsonArray();
        map.values().forEach(v -> arr.add(asTree(v)));

        return arr;
    }

    public static <T> @NotNull Map<String, T> arrToMap(@NotNull JsonArray arr, String key) {
        ConcurrentHashMap<String, T> map = new ConcurrentHashMap<>();
        arrAsStream(arr).forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            String k = keyAsStr(obj, key);
            if (k == null) return;
            
            map.put(k, deserialize(el, getType(el)));
        });

        return map;
    }

    public static int[] arrToIntArr(@NotNull JsonArray arr) {
        return convert(arr, int[].class);
    }

    public static Stream<JsonElement> arrAsStream(@NotNull JsonArray arr) {
        return arr.asList().parallelStream();
    }

    public static Stream<Map.Entry<String, JsonElement>> streamEntries(@NotNull JsonObject o) {
        return streamEntries(o.asMap());
    }

    public static <T> Stream<Map.Entry<String, T>> streamEntries(@NotNull Map<String, T> o) {
        return o.entrySet().parallelStream();
    }

    public static <T> Stream<T> streamValues(@NotNull Map<String, T> o) {
        return o.values().parallelStream();
    }

    public static Map<String, JsonObject> intersection(JsonArray arr, JsonArray arr2) {
        return arrAsStream(arr).flatMap(obj -> arrAsStream(arr2)
                .map(JsonElement::getAsJsonObject)
                .filter(obj2 -> Objects.equals(member(obj2, "name"), obj))
        ).collect(Collectors.toMap(obj -> keyAsStr(obj, "name"), obj -> obj));
    }

//    public static Map<String, Player> difference(JsonArray ops, JsonArray residents) {
//        return difference(ops, residents, "name");
//    }
//
//    public static Map<String, Player> difference(JsonArray ops, JsonArray residents, String key) {
//        Set<String> names = arrAsStream(residents).filter(Objects::nonNull)
//                .map(res -> keyAsStr(res.getAsJsonObject(), key))
//                .collect(Collectors.toSet());
//
//        Type playerListType = new TypeToken<List<Player>>(){}.getType();
//        List<Player> playerList = deserialize(serialize(ops), playerListType);
//
//        return playerList.parallelStream()
//                .filter(Objects::nonNull)
//                .filter(op -> !names.contains(op.getName()))
//                .collect(Collectors.toMap(BaseEntity::getName, Function.identity()));
//    }

//    public static <T> JsonObject valueAsObj(Map.Entry<String, T> entry) {
//        return (JsonObject) entryVal(entry);
//    }
//
//    static <T> T entryVal(@NotNull Map.Entry<String, T> entry) {
//        return entry.getValue();
//    }

    static JsonElement member(JsonObject o, String k) {
        return o == null ? null : o.get(k);
    }

    static boolean isNull(JsonElement el) {
        return el == JsonNull.INSTANCE || el == null;
    }

    public static @Nullable JsonObject keyAsObj(JsonObject o, String k) {
        JsonElement key = member(o, k);
        JsonObject result = new JsonObject();

        key.getAsJsonArray().forEach(el -> result.add(k, el));

        return isNull(key) ? null : result;
    }

    @Nullable
    public static Boolean keyAsBool(JsonObject o, String k) {
        JsonElement key = member(o, k);
        return isNull(key) ? null : key.getAsBoolean();
    }

    @Nullable
    public static Integer keyAsInt(JsonObject o, String k) {
        JsonElement key = member(o, k);
        return isNull(key) ? null : key.getAsInt();
    }

    @Nullable
    public static String keyAsStr(JsonObject o, String k) {
        JsonElement key = member(o, k);
        return isNull(key) ? null : key.getAsString();
    }

    @Nullable
    public static Double keyAsDouble(JsonObject o, String k) {
        JsonElement key = member(o, k);
        return isNull(key) ? null : key.getAsDouble();
    }

    public static JsonArray keyAsArr(JsonObject obj, String key) {
        JsonArray arr = new JsonArray();

        try { arr = requireNonNull(member(obj, key)).getAsJsonArray(); }
        catch (IllegalStateException e) {
            arr.add(obj.get(key));
        }

        return arr;
    }

    public static @NotNull JsonObject mapToObj(@NotNull Map<String, JsonObject> map) {
        JsonObject obj = new JsonObject();
        map.forEach(obj::add);
        return obj;
    }

    public static JsonArray arrAsJsonArray(Object[] objects) {
        return asTree(List.of(objects)).getAsJsonArray();
    }
}
package io.github.emcw.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.github.emcw.objects.Base;
import io.github.emcw.objects.Player;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("unchecked")
public class GsonUtil {
    @Getter
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Color.class, new ColorAdapter())
            .setPrettyPrinting()
            .create();

    static String regex = "(?<=})\\s*,\\s*(?=\\{)";

    public static <T> String serialize(Object obj) {
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

    public static JsonElement asTree(Object input) {
        return getGSON().toJsonTree(input);
    }

    public static <T> List<T> toList(Object obj) {
       return (List<T>) deserialize(serialize(obj), List.class);
    }

    public static <T> JsonArray mapToArr(@NotNull Map<String, T> map) {
        JsonArray arr = new JsonArray();
        map.values().parallelStream().forEach(v -> arr.add(asTree(v)));

        return arr;
    }

    public static <T> Map<String, T> arrToMap(@NotNull JsonArray arr, String key) {
        ConcurrentHashMap<String, T> map = new ConcurrentHashMap<>();
        arrAsStream(arr).forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            map.put(obj.get(key).getAsString(), deserialize(el, getType(el)));
        });

        return map;
    }

    public static int[] arrToIntArr(@NotNull JsonArray arr) {
        return deserialize(serialize(arr), int[].class);
    }

    public static @NotNull JsonArray arrFromStrArr(String @NotNull [] obj) {
        JsonArray arr = new JsonArray();
        for (String value : obj) {
            arr.add(deserialize(value, JsonElement.class));
        }

        return arr;
    }

    public static Stream<String> strArrAsStream(@NotNull String[] arr) {
        return Arrays.stream(arr).toList().parallelStream();
    }

    public static Stream<JsonElement> arrAsStream(@NotNull JsonArray arr) {
        return arr.asList().parallelStream();
    }

    public static Stream<Map.Entry<String, JsonElement>> streamEntries(@NotNull JsonObject o) {
        return o.entrySet().parallelStream();
    }

    public static <T> Stream<Map.Entry<String, T>> streamEntries(@NotNull Map<String, T> o) {
        return o.entrySet().parallelStream();
    }

    public static Map<String, JsonObject> intersection(JsonArray arr, JsonArray arr2) {
        return arrAsStream(arr).flatMap(obj -> arrAsStream(arr2)
                .map(JsonElement::getAsJsonObject)
                .filter(obj2 -> Objects.equals(member(obj2, "name"), obj))
        ).collect(Collectors.toMap(obj -> keyAsStr(obj, "name"), obj -> obj));
    }

    public static Map<String, Player> difference(JsonArray ops, JsonArray residents) {
        return difference(ops, residents, "name");
    }

    public static Map<String, Player> difference(JsonArray ops, JsonArray residents, String key) {
        Set<String> names = arrAsStream(residents).filter(Objects::nonNull)
                .map(res -> keyAsStr(res.getAsJsonObject(), key))
                .collect(Collectors.toSet());

        Type playerListType = new TypeToken<List<Player>>(){}.getType();
        List<Player> playerList = deserialize(serialize(ops), playerListType);

        return playerList.parallelStream()
                .filter(Objects::nonNull)
                .filter(op -> !names.contains(op.getName()))
                .collect(Collectors.toMap(Base::getName, Function.identity()));
    }

    static JsonObject valueAsObj(@NotNull Map.Entry<String, JsonElement> entry) {
        return entry.getValue().getAsJsonObject();
    }

    static JsonElement member(@NotNull JsonObject o, String k) {
        return o.get(k);
    }

    static boolean isNull(JsonElement el) {
        return el == JsonNull.INSTANCE || el == null;
    }

    public static @NotNull Boolean keyAsBool(JsonObject o, String k) {
        JsonElement key = member(o, k);
        return Boolean.TRUE.equals(key == null ? null : key.getAsBoolean());
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
}
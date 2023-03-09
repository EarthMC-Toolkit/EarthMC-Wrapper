package io.github.emcw.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

public class GsonUtil {
    @Getter
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ForkJoinPool pool = new ForkJoinPool();
    static String regex = "(?<=})\\s*,\\s*(?=\\{)";

    @Nullable
    static JsonElement member(@NotNull JsonObject o, String k) {
        return o.get(k);
    }

    public static <T> String serialize(Object obj) {
        Type collectionType = new TypeToken<T>() {}.getType();
        return GSON.toJson(obj, collectionType);
    }

    public static <T> T deserialize(String str, Class<T> c) {
        return GSON.fromJson(str, c);
    }

    public static JsonElement asTree(Object input) {
        return getGSON().toJsonTree(input);
    }

    public static <T> List<T> toList(Object obj) {
       String json = serialize(obj);
       return (List<T>) deserialize(json, List.class);
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

    static Stream<JsonElement> arrAsStream(@NotNull JsonArray obj) {
        return obj.asList().parallelStream();
    }

    static JsonObject valueAsObj(@NotNull Map.Entry<String, JsonElement> entry) {
        return entry.getValue().getAsJsonObject();
    }

    @Nullable
    public static JsonElement getKey(JsonObject obj, String key) {
        try { return member(obj, key); }
        catch (Exception e) { return null; }
    }

    public static @NotNull Boolean keyAsBool(JsonObject o, String k) {
        JsonElement key = getKey(o, k);
        return Boolean.TRUE.equals(key == null ? null : key.getAsBoolean());
    }

    @Nullable
    public static Integer keyAsInt(JsonObject o, String k) {
        JsonElement key = getKey(o, k);
        return key == JsonNull.INSTANCE || key == null ? null : key.getAsInt();
    }

    @Nullable
    public static String keyAsStr(JsonObject o, String k) {
        JsonElement key = getKey(o, k);
        return key == JsonNull.INSTANCE || key == null ? null : key.getAsString();
    }

    public static JsonArray keyAsArr(JsonObject obj, String key) {
        JsonArray arr = new JsonArray();

        try { arr = Objects.requireNonNull(member(obj, key)).getAsJsonArray(); }
        catch (IllegalStateException e) {
            arr.add(obj.get(key));
        }

        return arr;
    }

    public static @NotNull JsonObject toObj(@NotNull Map<String, JsonObject> map) {
        JsonObject obj = new JsonObject();
        map.forEach(obj::add);
        return obj;
    }
}
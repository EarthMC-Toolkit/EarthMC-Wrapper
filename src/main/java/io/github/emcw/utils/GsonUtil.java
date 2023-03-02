package io.github.emcw.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GsonUtil {
    @Getter
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Nullable
    static JsonElement member(JsonObject o, String k) {
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
       return GSON.fromJson(json, new TypeToken<List<T>>() {}.getType());
    }

    public static JsonElement arrFromStrArr(String[] obj) {
        JsonArray arr = new JsonArray();
        for (String value : obj) {
            arr.add(deserialize(value, JsonElement.class));
        }

        return arr;
    }

    static JsonObject valueAsObj(Map.Entry<String, JsonElement> entry) {
        return entry.getValue().getAsJsonObject();
    }

    @Nullable
    public static JsonElement getKey(JsonObject obj, String key) {
        try { return member(obj, key); }
        catch (Exception e) { return null; }
    }

    @Nullable
    public static Boolean keyAsBool(JsonObject o, String k) {
        JsonElement key = getKey(o, k);
        return key == JsonNull.INSTANCE || key == null ? null : key.getAsBoolean();
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
}
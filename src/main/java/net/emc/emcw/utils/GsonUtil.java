package net.emc.emcw.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class GsonUtil {
    @Getter
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

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

    public static JsonElement arrFromStrArr(String[] obj) {
        JsonArray arr = new JsonArray();
        for (String value : obj) {
            arr.add(deserialize(value, JsonElement.class));
        }

        return arr;
    }

    public static Integer keyAsInt(JsonObject obj, String key) {
        try { return member(obj, key).getAsInt(); }
        catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static String keyAsStr(JsonObject obj, String key) {
        try { return member(obj, key).getAsString(); }
        catch (Exception e) {
            return null;
        }
    }

    public static JsonArray keyAsArr(JsonObject obj, String key) {
        JsonArray arr = new JsonArray();

        try { arr = member(obj, key).getAsJsonArray(); }
        catch (IllegalStateException e) {
            arr.add(obj.get(key));
        }

        return arr;
    }

    @Nullable
    public static Boolean keyAsBool(JsonObject obj, String key) {
        try { return member(obj, key).getAsBoolean(); }
        catch (Exception e) {
            return null;
        }
    }
}
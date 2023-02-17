package net.emc.emcw.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

public class GsonUtil {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static JsonElement member(JsonObject o, String k) {
        return o.get(k);
    }

    public static String stringify(Object obj) {
        return GSON.toJson(obj);
    }

    public static Integer keyAsInt(JsonObject obj, String key) {
        try { return member(obj, key).getAsInt(); }
        catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public static String keyAsStr(JsonObject obj, String key) {
        try { return member(obj, key).getAsString(); }
        catch (UnsupportedOperationException e) {
            return null;
        }
    }

    @Nullable
    public static Boolean keyAsBool(JsonObject obj, String key) {
        try { return member(obj, key).getAsBoolean(); }
        catch (UnsupportedOperationException e) {
            return null;
        }
    }
}
package net.emc.emcw.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    public static String keyAsStr(JsonObject obj, String key) {
        try { return member(obj, key).getAsString(); }
        catch (NumberFormatException e) {
            return null;
        }
    }
}
package io.github.emcw.utils.parsers;

import com.google.gson.JsonObject;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.List;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BaseParser {
    //static final Safelist whitelist = new Safelist().addAttributes("a", "href");

    static final Cache<String, JsonObject> rawTowns = buildEmpty();
    static final Cache<String, JsonObject> rawNations = buildEmpty();
    static final Cache<String, JsonObject> rawResidents = buildEmpty();
    static final Cache<String, JsonObject> rawPlayers = buildEmpty();

    @NotNull @Contract(" -> new")
    static <K, V> Cache<K, V> buildEmpty() {
        return Caffeine.newBuilder().build();
    }

//    public static List<String> processFlags(@NotNull String str) {
//        return strArrAsStream(str.split("<br />"))
//            .map(e -> Jsoup.clean(e, whitelist))
//            .collect(Collectors.toList());
//    }

    /**
     * Given the list of flags (string in the format <code>"Key: boolean"</code>), get the flag at the
     * <code>index</code> and extract the value of the given <code>key</code> as a boolean.
     * @param kbStrings The list of flag strings.
     * @param index The index of the list we want the flag string from.
     * @param key The substring to slice off. Example: <code>"PVP: "</code>
     * @return True if the key was true, False if the key was false or not a boolean.
     */
    static boolean flagAsBool(@NotNull List<String> kbStrings, Integer index, String key) {
        String str = kbStrings.get(index).replace(key, "");
        return Boolean.parseBoolean(str);
    }
}
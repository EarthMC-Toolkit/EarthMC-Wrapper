package io.github.emcw.utils.parsers;

import com.google.gson.JsonObject;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static io.github.emcw.utils.GsonUtil.strArrAsStream;

public class BaseParser {
    static final Safelist whitelist = new Safelist().addAttributes("a", "href");

    static final Cache<String, JsonObject> rawTowns = buildEmpty();
    static final Cache<String, JsonObject> rawNations = buildEmpty();
    static final Cache<String, JsonObject> rawResidents = buildEmpty();
    static final Cache<String, JsonObject> rawPlayers = buildEmpty();

    @Contract(" -> new")
    static <K, V> @NotNull Cache<K, V> buildEmpty() {
        return Caffeine.newBuilder().build();
    }

    public static List<String> processFlags(@NotNull String str) {
        return strArrAsStream(str.split("<br />"))
            .map(e -> Jsoup.clean(e, whitelist))
            .collect(Collectors.toList());
    }

    static boolean flagAsBool(@NotNull List<String> info, Integer index, String key) {
        String str = info.get(index).replace(key, "");
        return Boolean.parseBoolean(str);
    }
}
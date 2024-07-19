package io.github.emcw.utils.parsers;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jsoup.safety.Safelist;

public class SquaremapParser {
    static final Safelist whitelist = new Safelist().addAttributes("a", "href");

    @Contract(" -> new")
    static <K, V> @NotNull Cache<K, V> buildEmpty() {
        return Caffeine.newBuilder().build();
    }
}
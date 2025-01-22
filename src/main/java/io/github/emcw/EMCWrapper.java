package io.github.emcw;

import io.github.emcw.caching.CacheOptions;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * The main entrypoint of this library.
 * <p>Holds an instance of itself and a reference to initialized maps.</p>
 */
@SuppressWarnings("unused, LombokGetterMayBeUsed, UnusedReturnValue")
public class EMCWrapper {
    static EMCWrapper instance = null;
    final HashMap<String, Squaremap> Squaremaps = new HashMap<>();

    public EMCWrapper() {
        instance = this;
    }

    public EMCWrapper registerSquaremap(@NotNull KnownMap map) {
        return registerSquaremap(map, true);
    }

    public EMCWrapper registerSquaremap(@NotNull KnownMap map, boolean prefill) {
        return registerSquaremap(map, null, null, true);
    }

    public EMCWrapper registerSquaremap(@NotNull KnownMap map, CacheOptions mapDataOpts, CacheOptions playerDataOpts, boolean prefill) {
        Squaremaps.put(map.getName(), new Squaremap(map, mapDataOpts, playerDataOpts, true));
        return this;
    }

    public Squaremap getSquaremap(@NotNull KnownMap map) {
        return Squaremaps.get(map.getName());
    }

    public static EMCWrapper instance() {
        return instance;
    }
}
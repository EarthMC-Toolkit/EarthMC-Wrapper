package io.github.emcw;

import java.util.HashMap;

/**
 * The main entrypoint of this library. Allows initializing maps independently.
 * <p>Holds an instance of itself and a reference to all maps.</p>
 */
@SuppressWarnings("unused, LombokGetterMayBeUsed")
public class EMCWrapper {
    private static EMCWrapper instance = null;
    private static final java.util.Map<String, EMCMap> Maps = new HashMap<>();

    /**
     * Initializes all known maps.
     * To only initialize the maps you need, simply provide any amount of KnownMap arguments.
     * @see #EMCWrapper(KnownMap...) 
     */
    public EMCWrapper() {
        this(KnownMap.values());
    }

    /**
     * Initializes the provided known maps.
     * @see EMCWrapper
     */
    public EMCWrapper(KnownMap... maps) {
        for (KnownMap map : maps) {
            initMap(new EMCMap(map));
        }

        instance = this;
    }

    private void initMap(EMCMap map) {
        Maps.put(map.mapName, map);
    }

    public EMCMap getMap(KnownMap map) {
        return Maps.get(map.getName());
    }

    public static EMCWrapper instance() {
        return instance;
    }
}
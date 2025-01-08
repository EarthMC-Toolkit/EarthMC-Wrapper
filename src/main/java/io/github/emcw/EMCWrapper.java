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
     * Returns a new wrapper instance and initialize all maps.
     */
    public EMCWrapper() {
        initMap(new EMCMap(KnownMap.AURORA));
        instance = this;
    }

    public EMCWrapper(EMCMap... maps) {
        for (EMCMap map : maps) {
            initMap(map);
        }
    }

    private void initMap(EMCMap map) {
        Maps.put(map.mapName, map);
    }

    public EMCMap getMap(KnownMap map) {
        return Maps.get(map.getName());
        //return Objects.equals(map.getName(), "nova") ? Nova : Aurora;
    }

    public static EMCWrapper instance() {
        return instance;
    }
}
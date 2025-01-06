package io.github.emcw;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * The main entrypoint of this library. Allows initializing maps independently.
 * <p>Holds an instance of {@link #Aurora} map as well as itself.</p>
 */
public class EMCWrapper {
    private static EMCWrapper instance = null;
    @Getter EMCMap Aurora;

    /**
     * Returns a new wrapper instance. Both maps are initialized by default.
     */
    public EMCWrapper() {
        initMaps(true);
    }

    public EMCWrapper(EMCMap aurora) {
        initMaps(aurora);
    }

    /**
     * Returns a new {@link EMCWrapper} instance.
     * Maps may be initialized independently by passing their respective boolean values.
     * @param aurora Enable initialization of the {@link #Aurora} map.
     */
    public EMCWrapper(Boolean aurora) {
        initMaps(aurora);
    }

    private void initMaps(@NotNull Boolean aurora) {
        if (aurora) Aurora = new EMCMap("aurora");

        instance = this;
    }

    private void initMaps(EMCMap aurora) {
        if (aurora != null) Aurora = aurora;

        instance = this;
    }

    public static EMCWrapper instance() {
        return instance;
    }
}
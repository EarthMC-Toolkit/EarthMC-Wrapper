package io.github.emcw.core;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * The main entrypoint of this library. Allows initializing maps independently.
 * <p>Holds an instance of {@link #Aurora} and {@link #Nova} maps as well as itself.</p>
 */
public class EMCWrapper {
    private static EMCWrapper instance = null;
    @Getter EMCMap Aurora, Nova;

    /**
     * Returns a new wrapper instance. Both maps are initialized by default.
     */
    public EMCWrapper() {
        initMaps(true, true);
    }

    public EMCWrapper(EMCMap aurora, EMCMap nova) {
        initMaps(aurora, nova);
    }

    /**
     * Returns a new {@link EMCWrapper} instance.
     * Maps may be initialized independently by passing their respective boolean values.
     * @param aurora Enable initialization of the {@link #Aurora} map.
     * @param nova Enable initialization of the {@link #Nova} map.
     */
    public EMCWrapper(Boolean aurora, Boolean nova) {
        initMaps(aurora, nova);
    }

    private void initMaps(@NotNull Boolean aurora, @NotNull Boolean nova) {
        if (aurora) Aurora = new EMCMap("aurora");
        if (nova) Nova = new EMCMap("nova");

        instance = this;
    }

    private void initMaps(EMCMap aurora, EMCMap nova) {
        if (aurora != null) Aurora = aurora;
        if (nova != null) Nova = nova;

        instance = this;
    }

    public static EMCWrapper instance() {
        return instance;
    }
}
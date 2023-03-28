package io.github.emcw.core;

import lombok.Getter;

enum MapType {
    AURORA,
    NOVA
}

public class EMCWrapper {
    private static EMCWrapper instance = null;
    @Getter EMCMap Aurora, Nova;

    public EMCWrapper() {
        new EMCWrapper(true, true);
    }

    public EMCWrapper(Boolean aurora, Boolean nova) {
        initMaps(aurora, nova);
    }

    public EMCWrapper(MapType map) {
        if (map == MapType.AURORA) initMaps(true, false);
        else initMaps(false, true);
    }

    private void initMaps(Boolean aurora, Boolean nova) {
        if (aurora) Aurora = new EMCMap("aurora");
        if (nova) Nova = new EMCMap("nova");

        instance = this;
    }

    public static EMCWrapper instance() {
        return instance;
    }
}
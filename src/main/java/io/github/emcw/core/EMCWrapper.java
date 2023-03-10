package io.github.emcw.core;

import lombok.Getter;

public class EMCWrapper {
    static EMCWrapper instance = null;
    @Getter EMCMap Aurora, Nova;

    public EMCWrapper(Boolean aurora, Boolean nova) {
        if (aurora) this.Aurora = new EMCMap("aurora");
        if (nova) this.Nova = new EMCMap("nova");

        instance = this;
    }

    public EMCWrapper() {
        new EMCWrapper(true, true);
    }

    public static EMCWrapper instance() {
        return instance;
    }
}
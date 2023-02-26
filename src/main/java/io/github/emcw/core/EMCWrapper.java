package io.github.emcw.core;

public class EMCWrapper {
    static EMCWrapper instance;

    public final EMCMap Aurora, Nova;

    public EMCWrapper() {
        this.Aurora = new EMCMap("aurora");
        this.Nova = new EMCMap("nova");

        instance = this;
    }

    public static EMCWrapper instance() {
        return instance;
    }
}
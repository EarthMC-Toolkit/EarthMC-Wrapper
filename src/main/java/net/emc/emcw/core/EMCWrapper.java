package net.emc.emcw.core;


public class EMCWrapper {
    static EMCWrapper instance;

    public final EMCMap Aurora, Nova;

    public EMCWrapper() {
        this.Aurora = new EMCMap("aurora");
        this.Nova = new EMCMap("nova");

        instance = this;
    }

    public static EMCWrapper getInstance() {
        return instance;
    }
}

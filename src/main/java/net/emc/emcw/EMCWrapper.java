package net.emc.emcw;

public class EMCWrapper {
    public static EMCWrapper instance;
    public String map;

    public EMCWrapper(String mapName) {
        this.map = mapName;
        instance = this;
    }

    public static EMCWrapper getInstance() {
        return instance;
    }
}

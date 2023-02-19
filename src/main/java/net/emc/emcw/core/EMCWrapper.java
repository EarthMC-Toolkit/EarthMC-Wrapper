package net.emc.emcw.core;

import net.emc.emcw.objects.Nation;
import net.emc.emcw.objects.Player;
import net.emc.emcw.objects.Town;

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
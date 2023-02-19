package net.emc.emcw.core;

import net.emc.emcw.classes.Nations;
import net.emc.emcw.classes.Players;
import net.emc.emcw.classes.Towns;

public class EMCMap {
    public Nations Nations;
    public Towns Towns;
    public Players Players;

    EMCMap(String map) {
        this.Nations = new Nations(map);
        this.Towns = new Towns(map);
        this.Players = new Players(map);
    }
}

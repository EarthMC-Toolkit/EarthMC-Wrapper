package io.github.emcw.core;

import io.github.emcw.classes.Nations;
import io.github.emcw.classes.Players;
import io.github.emcw.classes.Towns;

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

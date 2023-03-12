package io.github.emcw.core;

import io.github.emcw.classes.Nations;
import io.github.emcw.classes.Players;
import io.github.emcw.classes.Residents;
import io.github.emcw.classes.Towns;
import lombok.Getter;

public class EMCMap {
    public Nations Nations;
    public Towns Towns;
    public Players Players;
    public Residents Residents;

    @Getter
    String map;

    EMCMap(String mapName) {
        this.map = mapName;

        this.Nations = new Nations(this);
        this.Towns = new Towns(this);
        this.Players = new Players(this);
    }
}

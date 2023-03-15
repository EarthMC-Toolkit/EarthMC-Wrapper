package io.github.emcw.core;

import io.github.emcw.classes.Nations;
import io.github.emcw.classes.Players;
import io.github.emcw.classes.Residents;
import io.github.emcw.classes.Towns;
import lombok.Getter;

public class EMCMap {
    public Towns Towns;
    public Nations Nations;
    public Players Players;
    public Residents Residents;

    @Getter String map;

    EMCMap(String mapName) {
        map = mapName;

        Towns = new Towns(this);
        Nations = new Nations(this);
        Players = new Players(this);
        Residents = new Residents(this);
    }
}

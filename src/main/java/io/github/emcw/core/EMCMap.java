package io.github.emcw.core;

import io.github.emcw.data.Nations;
import io.github.emcw.data.Players;
import io.github.emcw.data.Residents;
import io.github.emcw.data.Towns;
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

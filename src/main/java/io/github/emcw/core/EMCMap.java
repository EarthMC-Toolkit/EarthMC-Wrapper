package io.github.emcw.core;

import io.github.emcw.caching.CacheStrategy;
import io.github.emcw.map.Nations;
import io.github.emcw.map.Players;
import io.github.emcw.map.Residents;
import io.github.emcw.map.Towns;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class EMCMap {
    @Getter String map;

    @Setter(AccessLevel.PRIVATE) public Towns Towns = null;
    @Setter(AccessLevel.PRIVATE) public Nations Nations = null;
    @Setter(AccessLevel.PRIVATE) public Players Players = null;
    @Setter(AccessLevel.PRIVATE) public Residents Residents = null;

    EMCMap(String mapName) {
        initMap(mapName, CacheStrategy.TIME_BASED);
    }

    EMCMap(String mapName, CacheStrategy cacheStrategy) {
        initMap(mapName, cacheStrategy);
    }

    void initMap(String mapName, CacheStrategy strategy) {
        map = mapName;

        setTowns(new Towns(this));
        setNations(new Nations(this));
        setPlayers(new Players(this));
        setResidents(new Residents(this));
    }
}

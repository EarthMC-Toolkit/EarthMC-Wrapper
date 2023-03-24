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

    @Setter(AccessLevel.PRIVATE) public CacheStrategy cacheStrategy;

    EMCMap(String mapName) {
        initMap(mapName, CacheStrategy.LAZY);
    }

    EMCMap(String mapName, CacheStrategy cacheStrategy) {
        initMap(mapName, cacheStrategy);
    }

    void initMap(String mapName, CacheStrategy strategy) {
        cacheStrategy = strategy;
        map = mapName;

        initAll();
    }

    private void initAll() {
        setTowns(new Towns(this));
        setNations(new Nations(this));
        setResidents(new Residents(this));
        setPlayers(new Players(this));

        updateAll();
    }

    private void updateAll() {
        Towns.updateCache(true);
        Nations.updateCache(true);
        Residents.updateCache(true);
        Players.updateCache(true);
    }
}

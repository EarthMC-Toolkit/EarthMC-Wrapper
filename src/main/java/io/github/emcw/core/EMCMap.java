package io.github.emcw.core;

import io.github.emcw.caching.CacheOptions;
import io.github.emcw.caching.CacheStrategy;
import io.github.emcw.map.Nations;
import io.github.emcw.map.Players;
import io.github.emcw.map.Residents;
import io.github.emcw.map.Towns;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

public class EMCMap {
    @Getter
    final String mapName;

    @Setter(AccessLevel.PRIVATE) public Towns Towns = null;
    @Setter(AccessLevel.PRIVATE) public Nations Nations = null;
    @Setter(AccessLevel.PRIVATE) public Players Players = null;
    @Setter(AccessLevel.PRIVATE) public Residents Residents = null;

    final CacheOptions lazyOpts = new CacheOptions(2, TimeUnit.SECONDS, CacheStrategy.LAZY);
    final CacheOptions timedOpts = new CacheOptions(3, TimeUnit.MINUTES, CacheStrategy.TIME_BASED);

    public EMCMap(String mapName) {
        this.mapName = mapName;
        initCaches();
    }

    private void initCaches() {
        setTowns(new Towns(this, timedOpts));
        setNations(new Nations(this, timedOpts));
        setResidents(new Residents(this, timedOpts));
        setPlayers(new Players(this, lazyOpts));

        prefill();
    }

    private void prefill() {
        Towns.forceUpdate();
        Nations.forceUpdate();
        Residents.forceUpdate();
        Players.forceUpdate();
    }
}
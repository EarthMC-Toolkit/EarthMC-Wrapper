package io.github.emcw;

import io.github.emcw.caching.CacheOptions;
import io.github.emcw.caching.CacheStrategy;
import io.github.emcw.map.api.Nations;
import io.github.emcw.map.api.Players;
import io.github.emcw.map.api.Residents;
import io.github.emcw.map.api.Towns;

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

    public EMCMap(String mapName, CacheOptions mapDataCache, CacheOptions playerDataCache) {
        this(mapName, mapDataCache, playerDataCache, false);
    }

    public EMCMap(String mapName, CacheOptions mapDataCache, CacheOptions playerDataCache, boolean prefill) {
        this.mapName = mapName;

        setTowns(new Towns(this, mapDataCache));
        setNations(new Nations(this, mapDataCache));
        setResidents(new Residents(this, mapDataCache));
        setPlayers(new Players(this, playerDataCache));

        if (prefill) prefill();
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
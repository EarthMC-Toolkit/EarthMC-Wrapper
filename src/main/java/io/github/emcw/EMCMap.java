package io.github.emcw;

import io.github.emcw.caching.CacheOptions;
import io.github.emcw.caching.CacheStrategy;
import io.github.emcw.map.api.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class EMCMap {
    @Getter final String mapName;

    @Setter(AccessLevel.PRIVATE) public Towns Towns = null;
    @Setter(AccessLevel.PRIVATE) public Nations Nations = null;
    @Setter(AccessLevel.PRIVATE) public Players Players = null;
    @Setter(AccessLevel.PRIVATE) public Residents Residents = null;
    //@Setter(AccessLevel.PRIVATE) public GPS GPS = null;

    final CacheOptions lazyOpts = new CacheOptions(2, TimeUnit.SECONDS, CacheStrategy.LAZY);
    final CacheOptions timedOpts = new CacheOptions(3, TimeUnit.MINUTES, CacheStrategy.TIME_BASED);

    EMCMap(@NotNull KnownMap map) {
        this.mapName = map.getName();

        initCaches();
        prefill();

        //setGPS(new GPS(this));
    }

//    EMCMap(String mapName, CacheOptions mapDataCache, CacheOptions playerDataCache, boolean prefill) {
//        this.mapName = mapName;
//
//        setTowns(new Towns(mapDataCache));
//        setNations(new Nations(mapDataCache));
//        setResidents(new Residents(mapDataCache));
//        setPlayers(new Players(this, playerDataCache));
//
//        if (prefill) prefill();
//
//        //setGPS(new GPS(this));
//    }

    private void initCaches() {
        Towns = new Towns(timedOpts);
        Nations = new Nations(timedOpts);
        Residents = new Residents(timedOpts);
        Players = new Players(this, lazyOpts);
    }

    private void prefill() {
        Towns.forceUpdate();
        Nations.forceUpdate();
        Residents.forceUpdate();
        Players.forceUpdate();
    }
}
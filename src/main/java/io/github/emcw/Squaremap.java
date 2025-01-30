package io.github.emcw;

import io.github.emcw.squaremap.SquaremapParser;
import io.github.emcw.squaremap.api.Nations;
import io.github.emcw.squaremap.api.Players;
import io.github.emcw.squaremap.api.Residents;
import io.github.emcw.squaremap.api.Towns;
import io.github.emcw.caching.CacheOptions;
import io.github.emcw.caching.CacheStrategy;

import lombok.Getter;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public class Squaremap {
    @Getter final String mapName;

    public final Towns Towns;
    public final Nations Nations;
    public final Players Players;
    public final Residents Residents;
    //public GPS GPS = null;

    @Getter final SquaremapParser parser;

    Squaremap(KnownMap map, @Nullable CacheOptions mapDataOpts, @Nullable CacheOptions playerDataOpts, boolean prefill) {
        this.mapName = map.getName();
        parser = new SquaremapParser(this.mapName);

        if (mapDataOpts == null) {
            mapDataOpts = new CacheOptions(CacheStrategy.TIME_BASED, 20, TimeUnit.SECONDS);
        }

        if (playerDataOpts == null) {
            playerDataOpts = new CacheOptions(CacheStrategy.LAZY, 2, TimeUnit.SECONDS);
        }

        this.Towns = new Towns(this.parser, mapDataOpts);
        this.Nations = new Nations(this.parser, mapDataOpts);
        this.Residents = new Residents(this.parser, mapDataOpts);
        this.Players = new Players(this.parser, this.Residents, playerDataOpts);

        if (prefill) {
            prefillCaches();
        }

        //GPS = new GPS(this));
    }

    private void prefillCaches() {
        this.Towns.forceUpdateCache();
        this.Nations.forceUpdateCache();
        this.Residents.forceUpdateCache();
        this.Players.forceUpdateCache();
    }
}
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

    public Towns Towns = null;
    public Nations Nations = null;
    public Players Players = null;
    public Residents Residents = null;
    //public GPS GPS = null;

    @Getter final SquaremapParser parser = new SquaremapParser();

    Squaremap(KnownMap map, @Nullable CacheOptions mapDataOpts, @Nullable CacheOptions playerDataOpts, boolean prefill) {
        this.mapName = map.getName();

        if (mapDataOpts == null) {
            mapDataOpts = new CacheOptions(CacheStrategy.TIME_BASED, 20, TimeUnit.SECONDS);
        }

        if (playerDataOpts == null) {
            playerDataOpts = new CacheOptions(CacheStrategy.LAZY, 2, TimeUnit.SECONDS);
        }

        init(mapDataOpts, playerDataOpts);
        if (prefill) prefill();

        //GPS = new GPS(this));
    }

    private void init(CacheOptions mapDataOpts, CacheOptions playerDataOpts) {
        Towns = new Towns(parser, mapDataOpts);
        Nations = new Nations(parser, mapDataOpts);
        Residents = new Residents(parser, mapDataOpts);
        Players = new Players(parser, Residents, playerDataOpts);
    }

    private void prefill() {
        Towns.forceUpdate();
        Nations.forceUpdate();
        Residents.forceUpdate();
        Players.forceUpdate();
    }
}
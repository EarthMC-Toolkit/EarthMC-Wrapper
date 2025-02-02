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
            // Always update after 20s.
            mapDataOpts = new CacheOptions(CacheStrategy.TIME_BASED, 20, TimeUnit.SECONDS);
        }

        if (playerDataOpts == null) {
            // Expire after 2 seconds, then update on next call.
            playerDataOpts = new CacheOptions(CacheStrategy.LAZY, 2, TimeUnit.SECONDS);
        }

        // It is recommended to keep them in this order as this is most natural according to how data is parsed.
        this.Towns = new Towns(this.parser, mapDataOpts);
        this.Nations = new Nations(this.parser, mapDataOpts);
        this.Residents = new Residents(this.parser, mapDataOpts);
        this.Players = new Players(this.parser, playerDataOpts);

        initDependencies();

        if (prefill) {
            prefillCaches();
        }

        //GPS = new GPS());
    }

    void initDependencies() {
        // TODO: Circular reference? Maybe combine these into just "Players" or redesign EMCW entirely.
        //       Although this is Java - it's GC doesn't rely on ref counting and can handle this just fine.
        this.Players.setResidents(this.Residents);
        this.Residents.setPlayers(this.Players);
    }

    // Essentially calls .updateCache(true) to force update using their respective implementation.
    private void prefillCaches() {
        this.Towns.forceUpdateCache();
        this.Nations.forceUpdateCache();
        this.Residents.forceUpdateCache();
        this.Players.forceUpdateCache();
    }
}
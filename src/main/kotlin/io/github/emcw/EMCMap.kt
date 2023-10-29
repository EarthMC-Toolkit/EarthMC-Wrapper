package io.github.emcw

import io.github.emcw.caching.CacheOptions
import io.github.emcw.caching.CacheStrategy
import io.github.emcw.map.api.Nations
import io.github.emcw.map.api.Players
import io.github.emcw.map.api.Residents
import io.github.emcw.map.api.Towns
import lombok.*
import java.util.concurrent.TimeUnit

class EMCMap {
    @Getter
    val mapName: String

    @Setter(AccessLevel.PRIVATE)
    var Towns: Towns? = null

    @JvmField
    @Setter(AccessLevel.PRIVATE)
    var Nations: Nations? = null

    @JvmField
    @Setter(AccessLevel.PRIVATE)
    var Players: Players? = null

    @Setter(AccessLevel.PRIVATE)
    var Residents: Residents? = null
    val lazyOpts = CacheOptions(2, TimeUnit.SECONDS, CacheStrategy.LAZY)
    val timedOpts = CacheOptions(3, TimeUnit.MINUTES, CacheStrategy.TIME_BASED)

    constructor(mapName: String) {
        this.mapName = mapName
        initCaches()
    }

    @JvmOverloads
    constructor(mapName: String, mapDataCache: CacheOptions, playerDataCache: CacheOptions, prefill: Boolean = false) {
        this.mapName = mapName
        setTowns(Towns(this, mapDataCache))
        setNations(Nations(this, mapDataCache))
        setResidents(Residents(this, mapDataCache))
        setPlayers(Players(this, playerDataCache))
        if (prefill) prefill()
    }

    private fun initCaches() {
        setTowns(Towns(this, timedOpts))
        setNations(Nations(this, timedOpts))
        setResidents(Residents(this, timedOpts))
        setPlayers(Players(this, lazyOpts))
        prefill()
    }

    private fun prefill() {
        Towns!!.forceUpdate()
        Nations!!.forceUpdate()
        Residents!!.forceUpdate()
        Players!!.forceUpdate()
    }
}
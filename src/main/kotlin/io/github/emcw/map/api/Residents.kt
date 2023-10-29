package io.github.emcw.map.api

import io.github.emcw.EMCMap
import io.github.emcw.caching.BaseCache
import io.github.emcw.caching.CacheOptions
import io.github.emcw.exceptions.MissingEntryException
import io.github.emcw.map.entities.Resident
import io.github.emcw.utils.DataParser.parseMapData
import io.github.emcw.utils.DataParser.parsedResidents

class Residents(private val parent: EMCMap, options: CacheOptions) : BaseCache<Resident?>(options) {
    init {
        setUpdater { forceUpdate() }
        forceUpdate()
        build()
    }

    fun tryUpdate() {
        tryExpire()
        updateCache(false)
    }

    fun forceUpdate() {
        updateCache(true)
    }

    private fun updateCache(force: Boolean) {
        if (!empty() && !force) return

        // Parse player data into usable Player objects.
        parseMapData(parent.mapName, false, false, true)
        val residents = parsedResidents()

        // Make sure we"re using valid data
        if (!residents.asMap().isEmpty()) setCache(residents)
    }

    override fun all(): Map<String?, Resident?>? {
        tryUpdate()
        return super.all()
    }

    @Throws(MissingEntryException::class)
    override fun single(name: String?): Resident? {
        tryUpdate()
        return super.single(name)
    }

    override fun get(vararg keys: String): Map<String?, Resident?>? {
        tryUpdate()
        return super.get(*keys)
    }
}
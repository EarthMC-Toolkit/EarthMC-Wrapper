package io.github.emcw.map.api

import io.github.emcw.EMCMap
import io.github.emcw.caching.BaseCache
import io.github.emcw.caching.CacheOptions
import io.github.emcw.exceptions.MissingEntryException
import io.github.emcw.interfaces.ILocatable
import io.github.emcw.map.entities.Town
import io.github.emcw.utils.DataParser.parseMapData
import io.github.emcw.utils.DataParser.parsedTowns

class Towns(private val parent: EMCMap, options: CacheOptions) : BaseCache<Town?>(options), ILocatable<Town?> {
    init {
        setUpdater { forceUpdate() }
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

        // Parse map data into usable Town objects.
        parseMapData(parent.mapName, true, false, true)
        val towns = parsedTowns()

        // Make sure were using valid data.
        if (!towns.asMap().isEmpty()) setCache(towns)
    }

    override fun all(): Map<String?, Town?>? {
        tryUpdate()
        return super.all()
    }

    @Throws(MissingEntryException::class)
    override fun single(name: String?): Town? {
        tryUpdate()
        return super.single(name)
    }

    override fun get(vararg keys: String): Map<String?, Town?>? {
        tryUpdate()
        return super.get(*keys)
    }
}
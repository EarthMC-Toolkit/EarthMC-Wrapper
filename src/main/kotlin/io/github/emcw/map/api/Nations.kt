package io.github.emcw.map.api

import io.github.emcw.EMCMap
import io.github.emcw.caching.BaseCache
import io.github.emcw.caching.CacheOptions
import io.github.emcw.exceptions.MissingEntryException
import io.github.emcw.interfaces.ILocatable
import io.github.emcw.map.entities.Nation
import io.github.emcw.utils.DataParser.parseMapData
import io.github.emcw.utils.DataParser.parsedNations

class Nations(private val parent: EMCMap, options: CacheOptions) : BaseCache<Nation?>(options), ILocatable<Nation?> {
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

        // Parse map data into usable Nation objects.
        parseMapData(parent.mapName, true, true, false)
        val nations = parsedNations(
            parent.mapName
        )
        if (!nations.asMap().isEmpty()) setCache(nations)
    }

    override fun all(): Map<String?, Nation?>? {
        tryUpdate()
        return super.all()
    }

    @Throws(MissingEntryException::class)
    override fun single(name: String?): Nation? {
        tryUpdate()
        return super.single(name)
    }

    override fun get(vararg keys: String): Map<String?, Nation?>? {
        tryUpdate()
        return super.get(*keys)
    }
}
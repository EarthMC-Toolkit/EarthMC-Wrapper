package io.github.emcw.map.api

import com.google.gson.JsonObject
import io.github.emcw.EMCMap
import io.github.emcw.caching.BaseCache
import io.github.emcw.caching.CacheOptions
import io.github.emcw.exceptions.MissingEntryException
import io.github.emcw.interfaces.ILocatable
import io.github.emcw.map.entities.Location
import io.github.emcw.map.entities.Player
import io.github.emcw.map.entities.Resident
import io.github.emcw.utils.DataParser.parsePlayerData
import io.github.emcw.utils.DataParser.parsedPlayers
import io.github.emcw.utils.Funcs
import io.github.emcw.utils.GsonUtil
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Suppress("unused")
class Players(private val parent: EMCMap, options: CacheOptions) : BaseCache<Player?>(options), ILocatable<Player?> {
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

    fun updateCache(force: Boolean?) {
        // We aren't forcing an update, and not expired.
        if (!empty() && !force!!) return

        // Parse player data into usable Player objects.
        parsePlayerData(parent.mapName)
        val players = parsedPlayers()

        // Make sure we have data to use.
        if (!players.asMap().isEmpty()) setCache(players)
    }

    override fun all(): Map<String?, Player?>? {
        // Merge residents & online players (townless will not include keys 'town', 'nation' and 'rank')
        return mergeWith(parent.Residents!!.all())
    }

    @Throws(MissingEntryException::class)
    override fun single(name: String?): Player? {
        tryUpdate()
        return super.single(name)
    }

    override fun get(vararg keys: String): Map<String?, Player?>? {
        tryUpdate()
        return super.get(*keys)
    }

    fun online(): Map<String?, Player?> {
        tryUpdate()
        return cache!!.asMap()
    }

    fun nearby(xCoord: Int?, zCoord: Int?, radius: Int?): Map<String?, Player?>? {
        return getNearby(online(), xCoord, zCoord, radius)
    }

    fun nearby(xCoord: Int?, zCoord: Int?, xRadius: Int?, zRadius: Int?): Map<String?, Player?>? {
        return getNearby(online(), xCoord, zCoord, xRadius, zRadius)
    }

    fun nearby(p: Player, xRadius: Int?, zRadius: Int?): Map<String?, Player?>? {
        val playerLoc: Location = p.getLocation()
        if (playerLoc.isDefault) return java.util.Map.of()
        val nearby = getNearby(online(), playerLoc.x, playerLoc.z, xRadius, zRadius)
        nearby!!.remove(p.name)
        return nearby
    }

    fun nearby(location: Location, xRadius: Int?, zRadius: Int?): Map<String?, Player?>? {
        return if (!location.valid()) java.util.Map.of() else getNearby(
            online(),
            location.x,
            location.z,
            xRadius,
            zRadius
        )
    }

    private fun mergeWith(residents: Map<String?, Resident?>?): Map<String?, Player?> {
        val ops = online()
        val merged: MutableMap<String?, Player?> = ConcurrentHashMap(ops)

        // Loop through residents in parallel
        GsonUtil.streamValues(residents).forEach { res: Resident? ->
            val resName = res!!.name
            val resObj = GsonUtil.asTree<JsonObject>(res)
            val found = ops[resName]
            val player = if (found == null) Resident(resObj) else Resident(GsonUtil.asTree(resObj), found)
            merged[resName] = player
        }

        // Remove null values from resulting map
        merged.values.removeIf { obj: Player? -> Objects.isNull(obj) }
        return merged
    }

    fun townless(): Map<String?, Player?>? {
        return Funcs.collectEntities(GsonUtil.streamValues(all()).filter { p: Player? -> !p!!.isResident() })
    }

    fun getOnline(playerName: String?): Player? {
        val map = online()
        return map.getOrDefault(playerName, null)
    }
}
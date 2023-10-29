package io.github.emcw.interfaces

import io.github.emcw.map.entities.Location
import io.github.emcw.map.entities.Nation
import io.github.emcw.map.entities.Player
import io.github.emcw.map.entities.Town
import io.github.emcw.utils.Funcs
import io.github.emcw.utils.GsonUtil

/**
 * This class specifies that an entity is locatable and provides methods to get other nearby locatable entities.
 * @param <T> The type of entity we are implementing this interface into.
 * Must be specified so the correct locations are returned when checking nearby.
</T> */
interface ILocatable<T> {
    fun getNearby(map: Map<String?, T>, xCoord: Int?, zCoord: Int?, radius: Int?): Map<String?, T?>? {
        return getNearby(map, xCoord, zCoord, radius, radius)
    }

    /**
     * Returns a new map of entities that are within the specified x and z radii given the coordinates.
     *
     *
     *
     * **Important notes: **
     *
     * Filtering is performed in parallel and is not guaranteed to be thread-safe.
     * In addition, the resulting map will be collected with any nulls removed.
     * @param map The map of entities to be checked against. Usually online players, but accepts a mix of Player, Resident, Town or Nation.
     * @param xCoord The x coordinate.
     * @param zCoord The z coordinate.
     * @param xRadius The radius to check within on the x-axis. (Left/Right)
     * @param zRadius The radius to check within on the z-axis. (Up/Down)
     * @return A new map of entities where each entry key is its name, and the value is itself.
     */
    fun getNearby(
        map: Map<String?, T>,
        xCoord: Int?, zCoord: Int?,
        xRadius: Int?, zRadius: Int?
    ): Map<String?, T?>? {
        INT_ARRAY_X[0] = xCoord
        INT_ARRAY_X[1] = xRadius
        INT_ARRAY_Z[0] = zCoord
        INT_ARRAY_Z[1] = zRadius
        return Funcs.collectAsMap(GsonUtil.streamEntries(map).filter { entry: Map.Entry<String?, T?>? ->
            checkNearby(
                entry!!.value
            )
        })
    }

    private fun checkNearby(`val`: T?): Boolean {
        var loc: Location? = null
        if (`val` is Player) {
            if (!`val`.visible()) return false
            loc = `val`.getLocation()
        } else if (`val` is Town) loc = `val`.location else if (`val` is Nation) loc = `val`.capital.location
        return loc != null && !loc.isDefault && isNearby(loc)
    }

    private fun isNearby(location: Location?): Boolean {
        if (location == null) return false
        val x = location.x
        val z = location.z
        return Funcs.withinRadius(x, INT_ARRAY_X) && Funcs.withinRadius(z, INT_ARRAY_Z)
    }

    companion object {
        val INT_ARRAY_X = arrayOfNulls<Int>(2)
        val INT_ARRAY_Z = arrayOfNulls<Int>(2)
    }
}
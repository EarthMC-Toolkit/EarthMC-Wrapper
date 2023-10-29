package io.github.emcw.map.entities

import com.google.gson.JsonObject
import io.github.emcw.interfaces.ISerializable
import io.github.emcw.utils.Funcs
import io.github.emcw.utils.GsonUtil
import lombok.Getter
import org.jetbrains.annotations.Contract

class Location
/**
 *
 * A location in 2D space.
 * @param x The coordinate on the x-axis (left/right)
 * @param z The coordinate on the z-axis (up/down)
 */(@field:Getter val x: Int?, @field:Getter val z: Int?) : ISerializable {
    @Getter
    var y: Int? = null
    /**
     *
     * A location in 3D space.
     * @param x The coordinate on the x-axis (left/right)
     * @param y The coordinate on the y-axis (up/down)
     * @param z The coordinate on the z-axis (forward/backward)
     */
    /**
     *
     * Default location representing 0, 64, 0.
     */
    @JvmOverloads
    constructor(x: Int? = 0, y: Int? = 64, z: Int? = 0) : this(x, z) {
        this.y = y
    }

    /**
     * Check if this location is properly initialized.
     * <br></br>Shorthand for `x != null && z != null;`
     */
    fun valid(): Boolean {
        return x != null && z != null
    }

    val isDefault: Boolean
        /**
         * Whether this location points to the map center.
         * Shorthand for `x == 0 && z == 0;`
         */
        get() = x == 0 && z == 0

    companion object {
        @Contract("_ -> new")
        protected fun fromObj(obj: JsonObject?): Location {
            return Location(GsonUtil.keyAsInt(obj, "x"), GsonUtil.keyAsInt(obj, "y"), GsonUtil.keyAsInt(obj, "z"))
        }

        fun of(obj: JsonObject): Location {
            val xArr = GsonUtil.keyAsArr(obj, "x")
            val zArr = GsonUtil.keyAsArr(obj, "z")
            val xAverage = Funcs.range(GsonUtil.arrToIntArr(xArr!!))
            val zAverage = Funcs.range(GsonUtil.arrToIntArr(zArr!!))
            return Location(xAverage, zAverage)
        }
    }
}
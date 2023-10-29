package io.github.emcw.map.entities

import com.google.gson.JsonObject
import io.github.emcw.interfaces.IPlayerCollective
import io.github.emcw.interfaces.ISerializable
import io.github.emcw.utils.GsonUtil
import lombok.Getter
import org.jetbrains.annotations.Contract
import java.awt.Color

@Suppress("unused")
class Town : BaseEntity<Town?>, IPlayerCollective, ISerializable {
    @Getter
    var mayor: String? = null

    @Getter
    var nation: String? = null

    @Getter
    var area: Int? = null

    @Getter
    var location: Location? = null

    @Getter
    var residents: List<Resident?>? = null

    @Getter
    var flags: Flags? = null

    @Getter
    var fill: Color? = null

    @Getter
    var outline: Color? = null

    constructor(obj: JsonObject) : super() {
        init(obj)
    }

    constructor(capital: Capital?) : super() {
        setInfo(this, capital!!.name)
        location = capital.location
    }

    fun init(obj: JsonObject) {
        setInfo(this, GsonUtil.keyAsStr(obj, "name"))
        nation = GsonUtil.keyAsStr(obj, "nation")
        mayor = GsonUtil.keyAsStr(obj, "mayor")
        residents = Resident.Companion.fromArr(GsonUtil.keyAsArr(obj, "residents"), "name")
        location = Location.Companion.of(obj)
        area = GsonUtil.keyAsInt(obj, "area")
        flags = Flags(obj)
        fill = getColour(GsonUtil.keyAsStr(obj, "fill"))
        outline = getColour(GsonUtil.keyAsStr(obj, "outline"))
    }

    class Flags(obj: JsonObject?) {
        val PVP: Boolean
        val EXPLOSIONS: Boolean
        val FIRE: Boolean
        val CAPITAL: Boolean
        val MOBS: Boolean
        val PUBLIC: Boolean

        init {
            PVP = GsonUtil.keyAsBool(obj, "pvp")
            EXPLOSIONS = GsonUtil.keyAsBool(obj, "explosions")
            FIRE = GsonUtil.keyAsBool(obj, "fire")
            CAPITAL = GsonUtil.keyAsBool(obj, "capital")
            MOBS = GsonUtil.keyAsBool(obj, "mobs")
            PUBLIC = GsonUtil.keyAsBool(obj, "public")
        }
    }

    fun onlineResidents(): Map<String?, Resident?>? {
        return onlineResidents(residents!!, parent!!)
    }

    fun nationless(): Boolean {
        return nation == null
    }

    fun getColour(hex: String?): Color {
        return Color.decode(hex ?: defaultColour())
    }

    fun defaultColour(): String {
        return defaultColour(nation)
    }

    companion object {
        @Contract(pure = true)
        private fun defaultColour(nationName: String?): String {
            return if (nationName == "No Nation") "#89C500" else "3FB4FF"
        }
    }
}
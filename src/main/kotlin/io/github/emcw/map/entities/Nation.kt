package io.github.emcw.map.entities

import com.google.gson.JsonObject
import io.github.emcw.exceptions.MissingEntryException
import io.github.emcw.interfaces.IPlayerCollective
import io.github.emcw.interfaces.ISerializable
import io.github.emcw.utils.Funcs
import io.github.emcw.utils.GsonUtil
import lombok.Getter

@Suppress("unused")
class Nation(obj: JsonObject, @field:Transient private val mapName: String) : BaseEntity<Nation?>(), IPlayerCollective,
    ISerializable {
    var capital: Capital? = null

    @Getter
    var towns: List<String?>? = null

    @Getter
    var residents: List<Resident?>? = null

    @Getter
    var leader: String? = null

    @Getter
    var area: Int? = null

    // Not exposed to serialization.
    @Transient
    private var residentNames: List<String?>? = null

    /**
     * Creates a new Nation by parsing raw data.<br></br>
     * <font color="#e38c1b">Should **NOT** be called explicitly unless you know what you are doing!</font>
     * @param obj The unparsed data required to build this object.
     */
    init {
        init(obj)
    }

    private fun init(obj: JsonObject) {
        setInfo(this, GsonUtil.keyAsStr(obj, "name"))
        leader = GsonUtil.keyAsStr(obj, "king")
        area = GsonUtil.keyAsInt(obj, "area")
        capital = Capital(obj.getAsJsonObject("capital"))
        towns = Funcs.removeListDuplicates(GsonUtil.toList(GsonUtil.keyAsArr(obj, "towns")))
        val residentArr = GsonUtil.keyAsArr(obj, "residents")
        residentNames = Funcs.removeListDuplicates(GsonUtil.toList(residentArr))
        residents = Resident.Companion.fromArr(residentArr!!, "name")
    }

    fun getCapital(): Town? {
        val towns = Funcs.mapInstance(mapName).Towns
        return try {
            towns!!.single(capital!!.name)
        } catch (e: MissingEntryException) {
            Town(capital)
        }
    }

    // TODO: Finish invitableTowns
    fun invitableTowns(): Map<String?, Town?>? {
        val towns = GsonUtil.streamEntries(
            Funcs.mapInstance(
                mapName
            ).Towns!!.all()
        )
        return Funcs.collectEntities(
            towns!!.map { entry: Map.Entry<String?, Town?>? ->
                val town = entry!!.value
                if (town!!.nation == null) {
                    val townLoc = town.getLocation()
                    val capitalLoc = getCapital().getLocation()

                    // In range, return the town
                    val inviteRange = if (mapName == "nova") 3000 else 3500
                    if (Funcs.manhattan(capitalLoc, townLoc) < inviteRange) {
                        return@map town
                    }
                }
                null
            }!!
        )
    }

    /**
     * Helper method to reduce mapping over [.residents] for names.
     * @return The names of residents in this nation.
     * @see .getResidents
     */
    fun residentList(): List<String?>? {
        return residentNames
    }

    /**
     * All residents that are online in this Nation.
     * @return A map of Residents with their entity [.name] being used as their respective keys.
     * @see .onlineResidents
     */
    fun onlineResidents(): Map<String?, Resident?>? {
        return onlineResidents(residents!!, parent!!)
    }
}
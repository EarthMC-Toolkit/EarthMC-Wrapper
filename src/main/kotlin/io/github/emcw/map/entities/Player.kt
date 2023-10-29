package io.github.emcw.map.entities

import com.google.gson.JsonObject
import io.github.emcw.EMCMap
import io.github.emcw.EMCWrapper.Companion.instance
import io.github.emcw.exceptions.MissingEntryException
import io.github.emcw.interfaces.ILocatable
import io.github.emcw.interfaces.ISerializable
import io.github.emcw.utils.GsonUtil
import lombok.Getter
import lombok.Setter

@Suppress("unused")
open class Player : BaseEntity<Player>, ISerializable, ILocatable<Player> {
    @Getter private var nickname: String? = null
    @Getter private var location: Location? = null

    @Transient private var world: String? = null

    @Setter @Transient var isResident: Boolean = false

    constructor(obj: JsonObject) : super() {
        init(obj, false)
        setLocation(obj, false)
    }

    constructor(obj: JsonObject, resident: Boolean) : super() {
        init(obj, resident)
        setLocation(obj, false)
    }

    constructor(obj: JsonObject, resident: Boolean, parsed: Boolean) : super() {
        init(obj, resident)
        setLocation(obj, parsed)
    }

    constructor(player: Player) : super() {
        setInfo(this, player.getName())
        nickname = player.getNickname()
        location = player.getLocation()
        isResident = player.isResident()
    }

    private fun init(obj: JsonObject, resident: Boolean) {
        setInfo(this, GsonUtil.keyAsStr(obj, "name"))
        nickname = GsonUtil.keyAsStr(obj, "nickname")
        world = GsonUtil.keyAsStr(obj, "world")
        isResident = resident
    }

    fun setLocation(obj: JsonObject, parsed: Boolean) {
        val loc = if (parsed) Location.fromObj(obj.getAsJsonObject("location")) else Location.fromObj(obj)
        if (loc.valid()) location = loc
    }

    /**
     * Converts this player into a [Resident].<br></br>
     * Essentially equivalent to a "downcast", adding new fields and methods found in [Resident],
     * keeping all existing info the same.
     * @param mapName The map used to retrieve the resident from. If invalid, Aurora will be assumed.
     * @return The [Resident] instance if found, otherwise a [MissingEntryException].
     */
    @Throws(MissingEntryException::class)
    fun asResident(mapName: String): Resident {
        val res = getMap(mapName).Residents.single(name)
        return Resident(GsonUtil.asTree(res), this)
    }

    /**
     * If this player has set a nickname.
     * @return true/false if [nickname] is same as their account [name].
     */
    fun hasCustomNickname(): Boolean {
        return nickname != null && nickname != name
    }

    /**
     * If this player is visible on the Dynmap.
     * @return true/false if [world] is "earth" and player is not under a block.
     */
    fun visible(): Boolean {
        return world == "earth"
    }

    /**
     * Essentially the opposite of [visible].
     *
     * **NOTE:**
     * This returns true for players under a tree, in the nether etc.
     * @return true/false if [world] is NOT "earth" and [location] is 0, 64, 0.
     */
    fun hidden(): Boolean {
        return locationIsDefault() && !visible()
    }

    /**
     * Whether this player is located at the default Dynmap location.
     * @return true/false if [location] is 0, 64, 0
     */
    fun locationIsDefault(): Boolean {
        return location!!.y == 64 && location!!.x == 0 && location!!.z == 0
    }

    /**
     * Check if this player is also a resident on the map this instance was retrieved from.
     */
    fun isResident(): Boolean {
        return isResident
    }

    /**
     *
     * Check if this player is online in the inputted map.
     * @return true/false if the player is online.
     */
    fun online(map: String): Boolean {
        return getMap(map).Players.online().containsKey(name)
    }

    companion object {
        private fun getMap(name: String): EMCMap {
            return if (name == "nova") instance().Nova else instance().Aurora
        }

        /**
         * Static helper method for retrieving an online [Player].
         * @param mapName The map this player is online in.<br></br> If invalid map is inputted, Aurora will be assumed.
         * @param playerName The name of the player we want to retrieve.
         * @return A new instance of this class
         */
        fun getOnline(mapName: String, playerName: String?): Player? {
            return getMap(mapName).Players.getOnline(playerName)
        }
    }
}
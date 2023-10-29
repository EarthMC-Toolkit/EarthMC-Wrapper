package io.github.emcw.map.entities

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.github.emcw.interfaces.ISerializable
import io.github.emcw.utils.GsonUtil
import lombok.Getter
import java.util.stream.Collectors
import java.util.stream.StreamSupport

class Resident : Player, ISerializable {
    @Getter
    private var town: String? = null

    @Getter
    private var nation: String? = null

    @Getter
    private var rank: String? = null

    constructor(res: JsonObject?, op: JsonObject?) : super(op!!, true, true) {
        setFields(res)
    }

    constructor(res: JsonObject?, op: Player?) : super(op!!) {
        setFields(res)
    }

    constructor(obj: JsonObject?) : super(obj!!, true) {
        setFields(obj)
    }

    fun setFields(obj: JsonObject?) {
        town = GsonUtil.keyAsStr(obj, "town")
        nation = GsonUtil.keyAsStr(obj, "nation")
        rank = GsonUtil.keyAsStr(obj, "rank")
    }

    /**
     *
     * Determines whether this resident has more permissions than a regular resident.
     * @return <font color="green">true</font> if [.rank] is 'Mayor' or 'Leader', otherwise <font color="red">false</font>.
     */
    fun hasAuthority(): Boolean {
        return rank == "Mayor" || rank == "Leader"
    }

    companion object {
        fun fromArr(arr: JsonArray, key: String?): List<Resident?> {
            return StreamSupport.stream(arr.spliterator(), true).map { curRes: JsonElement? ->
                val obj = JsonObject()
                obj.add(key, curRes)
                Resident(obj)
            }.collect(Collectors.toList())
        }
    }
}
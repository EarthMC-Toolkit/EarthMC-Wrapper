package io.github.emcw.interfaces

import io.github.emcw.map.entities.BaseEntity
import io.github.emcw.map.entities.Resident
import io.github.emcw.utils.Funcs
import java.util.function.Function
import java.util.stream.Collectors

interface IPlayerCollective {
    /**
     * Returns a new list of residents who are found to be online in the parent map.
     * @param residents The [Resident] list to perform filtering on.
     * @param parent The parent of this entity who provides the map name of the current instance.
     * @return A new list of [Resident] objects who are online.
     */
    fun onlineResidents(residents: List<Resident?>, parent: BaseEntity<*>): Map<String, Resident?>? {
        return Funcs.streamList(residents).filter { p: Resident? ->
            p!!.online(
                parent.name!!
            )
        }
            .collect(Collectors.toMap(BaseEntity::name, Function { r: Resident? -> r }))
    }
}
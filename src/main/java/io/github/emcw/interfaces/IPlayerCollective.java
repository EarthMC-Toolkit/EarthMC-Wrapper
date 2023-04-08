package io.github.emcw.interfaces;

import io.github.emcw.entities.BaseEntity;
import io.github.emcw.entities.Resident;
import io.github.emcw.utils.Funcs;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface IPlayerCollective {
    /**
     * Returns a new list of residents who are found to be online in the parent map.
     * @param residents The {@link io.github.emcw.entities.Resident} list to perform filtering on.
     * @param parent The parent of this entity who provides the map name of the current instance.
     * @return A new list of {@link io.github.emcw.entities.Resident} objects who are online.
     */
    default Map<String, Resident> onlineResidents(List<Resident> residents, BaseEntity<?> parent) {
        return Funcs.streamList(residents).filter(p -> p.online(parent.getName()))
                .collect(Collectors.toMap(BaseEntity::getName, r -> r));
    }
}
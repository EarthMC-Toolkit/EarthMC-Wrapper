package io.github.emcw.interfaces;

import io.github.emcw.entities.BaseEntity;
import io.github.emcw.entities.Resident;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface IPlayerCollective {
    default Map<String, Resident> onlineResidents(List<Resident> list, BaseEntity<?> parent) {
        return list.parallelStream().filter(p -> p.online(parent.getName()))
                .collect(Collectors.toMap(BaseEntity::getName, r -> r));
    }
}

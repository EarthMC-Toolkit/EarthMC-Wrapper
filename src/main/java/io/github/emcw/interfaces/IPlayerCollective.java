package io.github.emcw.interfaces;

import io.github.emcw.objects.Base;
import io.github.emcw.objects.Player;
import io.github.emcw.objects.Resident;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface IPlayerCollective {
    default Map<String, Resident> onlineResidents(List<? extends Player> list, Base<?> parent) {
        return (Map<String, Resident>) list.parallelStream()
                .filter(p -> p.online(parent.getName()))
                .collect(Collectors.toMap(Base::getName, r -> r));
    }
}

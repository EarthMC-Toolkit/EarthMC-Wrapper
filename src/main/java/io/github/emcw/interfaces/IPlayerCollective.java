package io.github.emcw.interfaces;

import io.github.emcw.objects.Base;
import io.github.emcw.objects.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface IPlayerCollective {
    default Map<String, ? extends Player> onlineResidents(List<? extends Player> list, Base<?> parent) {
        return list.parallelStream()
                .filter(p -> p.online(parent.getName()))
                .collect(Collectors.toMap(Base::getName, r -> r));
    }
}

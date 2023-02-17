package net.emc.emcw.classes;

import com.google.gson.JsonArray;
import net.emc.emcw.objects.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Players {
    static Map<String, Player> cache;

    public static List<Player> fromArray(JsonArray arr) {
        return arr.asList().stream()
                .map(p -> new Player(p.getAsJsonObject()))
                .collect(Collectors.toList());
    }
}

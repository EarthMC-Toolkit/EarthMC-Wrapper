package net.emc.emcw.objects.parsed;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

import static net.emc.emcw.utils.GsonUtil.keyAsStr;

public class Resident extends Player {
    public final String town, nation, rank;

    Resident(JsonObject obj) {
        super(obj);

        this.town = keyAsStr(obj, "town");
        this.nation = keyAsStr(obj, "nation");
        this.rank = keyAsStr(obj, "rank");
    }

    public static List<Resident> fromArray(JsonArray arr) {
        return arr.asList().stream()
                .map(p -> new Resident(p.getAsJsonObject()))
                .collect( Collectors.toList());
    }
}
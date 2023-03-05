package io.github.emcw.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.emcw.utils.Generics;
import io.github.emcw.utils.GsonUtil;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import static io.github.emcw.utils.GsonUtil.keyAsStr;

public class Nation {
    @Getter
    Capital capital;
    @Getter
    List<String> towns, residents;
    @Getter
    String name, leader;
    @Getter
    Integer area;

    public Nation(JsonObject obj) {
        this.name = keyAsStr(obj, "name");
        //this.leader = keyAsStr(obj, "leader");
        //this.area = keyAsInt(obj, "area");

        //String capitalName = keyAsStr(obj.getAsJsonObject("capital"), "name");
        //Location capitalLoc = new Location(keyAsInt(obj, "x"), keyAsInt(obj, "z"));

        //this.capital = new Capital(capitalName, capitalLoc);

        this.towns = GsonUtil.toList(obj.getAsJsonArray("towns"));
        //this.residents =
    }
}

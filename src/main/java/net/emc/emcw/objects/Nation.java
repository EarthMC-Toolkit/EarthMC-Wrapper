package net.emc.emcw.objects;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.emc.emcw.classes.Towns;

import java.util.List;

import static net.emc.emcw.utils.GsonUtil.keyAsInt;
import static net.emc.emcw.utils.GsonUtil.keyAsStr;

public class Nation {
    @Getter
    Capital capital;
    @Getter
    List<Town> towns;

    @Getter
    String name, leader;
    @Getter
    Integer area;

    public Nation(JsonObject obj) {
        this.name = keyAsStr(obj, "name");
        this.leader = keyAsStr(obj, "leader");
        this.area = keyAsInt(obj, "area");

        String capitalName = keyAsStr(obj.getAsJsonObject("capital"), "name");
        Location capitalLoc = new Location(keyAsInt(obj, "x"), keyAsInt(obj, "z"));

        this.capital = new Capital(capitalName, capitalLoc);
        //this.towns = Towns.fromArray(obj.getAsJsonArray("towns"));
    }

//    public static Nation fromTowns(List<Town> towns) {
//        return null;
//    }
}

package io.github.emcw.objects;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.github.emcw.classes.Towns;
import io.github.emcw.utils.GsonUtil;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.List;

import static io.github.emcw.utils.GsonUtil.*;

public class Nation {
    @Getter
    Capital capital;
    @Getter
    List<String> towns;

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
    }

//    public static Nation fromTowns(List<Town> towns) {
//        return null;
//    }
}

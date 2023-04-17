package io.github.emcw.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.emcw.interfaces.IPlayerCollective;
import io.github.emcw.interfaces.ISerializable;
import io.github.emcw.utils.Funcs;
import lombok.Getter;

import java.util.List;
import java.util.Map;

import static io.github.emcw.utils.GsonUtil.*;

public class Nation extends BaseEntity<Nation> implements IPlayerCollective, ISerializable {
    @Getter Capital capital;
    @Getter List<String> towns;
    @Getter List<Resident> residents;
    @Getter String leader;
    @Getter Integer area;

    // Not exposed to serialization.
    private transient List<String> residentNames;

    public Nation(JsonObject obj) {
        super();
        init(obj);
    }

    public void init(JsonObject obj) {
        setInfo(this, keyAsStr(obj, "name"));

        leader = keyAsStr(obj, "king");
        area = keyAsInt(obj, "area");
        capital = new Capital(obj.getAsJsonObject("capital"));
        towns = Funcs.removeListDuplicates(toList(keyAsArr(obj, "towns")));

        JsonArray residentArr = keyAsArr(obj, "residents");
        residentNames = Funcs.removeListDuplicates(toList(residentArr));
        residents = Resident.fromArr(residentArr, "name");
    }

    public List<String> residentList() {
        return residentNames;
    }

    public Map<String, Resident> onlineResidents() {
        return onlineResidents(residents, parent);
    }
}
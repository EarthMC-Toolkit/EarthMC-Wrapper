package io.github.emcw.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.emcw.interfaces.IPlayerCollective;
import io.github.emcw.interfaces.ISerializable;
import lombok.Getter;

import java.util.List;
import java.util.Map;

import static io.github.emcw.utils.GsonUtil.*;

public class Nation extends Base<Nation> implements IPlayerCollective, ISerializable {
    @Getter Capital capital;
    @Getter List<String> towns, residents;
    @Getter String leader;
    @Getter Integer area;

    // Not exposed to serialization.
    private transient List<Resident> residentList;

    public Nation(JsonObject obj) {
        super();
        init(obj);
    }

    public void init(JsonObject obj) {
        setInfo(this, keyAsStr(obj, "name"));

        leader = keyAsStr(obj, "king");
        area = keyAsInt(obj, "area");
        capital = new Capital(obj.getAsJsonObject("capital"));
        towns = toList(keyAsArr(obj, "towns"));

        JsonArray residentArr = keyAsArr(obj, "residents");
        residents = toList(residentArr);
        residentList = Resident.fromArr(residentArr, "name");
    }

    public List<Resident> residentList() {
        return residentList;
    }

    public Map<String, Resident> onlineResidents() {
        return onlineResidents(residentList, parent);
    }
}

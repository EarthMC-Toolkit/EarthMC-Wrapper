package io.github.emcw.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.emcw.utils.GsonUtil;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.emcw.utils.GsonUtil.*;

public class Nation extends Base<Nation> {
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
        towns = GsonUtil.toList(keyAsArr(obj, "towns"));

        JsonArray residentArr = keyAsArr(obj, "residents");
        residents = Resident.fromArr(residentArr);
        residentNames = toList(residentArr);
    }

    public List<String> residentNameList() {
        return residentNames;
    }

    public Map<String, Resident> onlineResidents() {
        return getResidents().parallelStream()
                .filter(p -> p.online(parent.getName()))
                .collect(Collectors.toMap(Base::getName, r -> r));
    }
}

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

    private final transient List<String> residentNames;

    public Nation(JsonObject obj) {
        super();
        setInfo(this, keyAsStr(obj, "name"));

        this.leader = keyAsStr(obj, "king");
        this.area = keyAsInt(obj, "area");
        this.capital = new Capital(obj.getAsJsonObject("capital"));
        this.towns = GsonUtil.toList(obj.getAsJsonArray("towns"));

        JsonArray residentArr = keyAsArr(obj, "residents");
        this.residentNames = toList(residentArr);
        this.residents = Resident.fromArr(residentArr);
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

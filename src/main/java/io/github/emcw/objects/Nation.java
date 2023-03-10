package io.github.emcw.objects;

import com.google.gson.JsonObject;
import io.github.emcw.utils.GsonUtil;
import lombok.Getter;

import java.util.List;

import static io.github.emcw.utils.GsonUtil.*;

public class Nation extends Base<Nation> {
    @Getter Capital capital;
    @Getter List<String> towns, residents;
    @Getter String leader;
    @Getter Integer area;

    public Nation(JsonObject obj) {
        super();
        setInfo(this, keyAsStr(obj, "name"));

        this.leader = keyAsStr(obj, "king");
        this.area = keyAsInt(obj, "area");
        this.capital = new Capital(obj.getAsJsonObject("capital"));
        this.towns = GsonUtil.toList(obj.getAsJsonArray("towns"));
        this.residents = toList(keyAsArr(obj, "residents"));
    }
}

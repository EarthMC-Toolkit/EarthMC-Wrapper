package io.github.emcw.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

import static io.github.emcw.utils.GsonUtil.*;

public class Resident extends Player {
    public final String town, nation, rank;

    public Resident(JsonObject obj) {
        super(obj, true);

        this.town = keyAsStr(obj, "town");
        this.nation = keyAsStr(obj, "nation");
        this.rank = keyAsStr(obj, "rank");
    }

    public static List<Resident> fromArr(JsonArray arr) {
        Iterator<JsonElement> itr = arr.iterator();
        List<Resident> list = new ArrayList<>();

        while (itr.hasNext()) {
            JsonObject obj = new JsonObject();
            obj.add("name", itr.next());
            list.add(new Resident(obj.getAsJsonObject()));
        }

        return list;
    }
}
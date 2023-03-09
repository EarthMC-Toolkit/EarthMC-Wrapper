package io.github.emcw.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static io.github.emcw.utils.GsonUtil.keyAsStr;

public class Resident extends Player {
    @Getter
    public final String town, nation, rank;

    public Resident(JsonObject obj) {
        super(obj, true);

        this.town = keyAsStr(obj, "town");
        this.nation = keyAsStr(obj, "nation");
        this.rank = keyAsStr(obj, "rank");
    }

    public static List<Resident> fromArr(JsonArray arr) {
        return StreamSupport.stream(arr.spliterator(), true).map(curRes -> {
            JsonObject obj = new JsonObject();
            obj.add("name", curRes);

            return new Resident(obj);
        }).collect(Collectors.toList());
    }
}
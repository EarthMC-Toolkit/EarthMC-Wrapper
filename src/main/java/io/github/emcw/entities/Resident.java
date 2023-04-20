package io.github.emcw.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.emcw.interfaces.ISerializable;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static io.github.emcw.utils.GsonUtil.keyAsStr;

@SuppressWarnings("unused")
public class Resident extends Player implements ISerializable {
    @Getter private String town, nation, rank;

    public Resident(JsonObject res, JsonObject op) {
        super(op, true, true);
        setFields(res);
    }

    public Resident(JsonObject res, Player op) {
        super(op);
        setFields(res);
    }

    public Resident(JsonObject obj) {
        super(obj, true);
        setFields(obj);
    }

    void setFields(JsonObject obj) {
        town = keyAsStr(obj, "town");
        nation = keyAsStr(obj, "nation");
        rank = keyAsStr(obj, "rank");
    }

    public boolean hasAuthority() {
        return Objects.equals(rank, "Mayor") || Objects.equals(rank, "Leader");
    }

    public static List<Resident> fromArr(JsonArray arr, String key) {
        return StreamSupport.stream(arr.spliterator(), true).map(curRes -> {
            JsonObject obj = new JsonObject();
            obj.add(key, curRes);

            return new Resident(obj);
        }).collect(Collectors.toList());
    }
}
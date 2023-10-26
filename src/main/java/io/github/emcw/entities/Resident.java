package io.github.emcw.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.emcw.interfaces.ISerializable;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static io.github.emcw.utils.GsonUtil.keyAsStr;

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

    /**
     * <p>Determines whether this resident has more permissions than a regular resident.</p>
     * @return <font color="green">true</font> if {@link #rank} is 'Mayor' or 'Leader', otherwise <font color="red">false</font>.
     */
    public boolean hasAuthority() {
        return Objects.equals(rank, "Mayor") || Objects.equals(rank, "Leader");
    }

    @SuppressWarnings("SameParameterValue")
    protected static List<Resident> fromArr(@NotNull JsonArray arr, String key) {
        return StreamSupport.stream(arr.spliterator(), true).map(curRes -> {
            JsonObject obj = new JsonObject();
            obj.add(key, curRes);

            return new Resident(obj);
        }).collect(Collectors.toList());
    }
}
package io.github.emcw.squaremap.entities;

import com.google.gson.JsonObject;
import io.github.emcw.interfaces.ISerializable;
import lombok.Getter;

import java.util.Objects;

import static io.github.emcw.utils.GsonUtil.keyAsStr;

@SuppressWarnings("unused")
public class SquaremapResident extends SquaremapPlayer implements ISerializable {
    @Getter private String town, nation, rank;

    public SquaremapResident(JsonObject res, JsonObject op) {
        super(op, true, true);
        setFields(res);
    }

    public SquaremapResident(JsonObject res, SquaremapPlayer op) {
        super(op);
        setFields(res);
    }

    public SquaremapResident(JsonObject obj) {
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

//    @SuppressWarnings("SameParameterValue")
//    protected static List<SquaremapResident> listFromJsonArr(@NotNull JsonArray arr, String key) {
//        return StreamSupport.stream(arr.spliterator(), true).map(curRes -> {
//            JsonObject obj = new JsonObject();
//            obj.add(key, curRes);
//
//            return new SquaremapResident(obj);
//        }).collect(Collectors.toList());
//    }
}
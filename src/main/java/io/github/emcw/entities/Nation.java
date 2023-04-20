package io.github.emcw.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.emcw.interfaces.IPlayerCollective;
import io.github.emcw.interfaces.ISerializable;
import io.github.emcw.map.Towns;
import io.github.emcw.utils.Funcs;
import lombok.Getter;

import java.util.List;
import java.util.Map;

import static io.github.emcw.utils.Funcs.collectAsMap;
import static io.github.emcw.utils.GsonUtil.*;

@SuppressWarnings("unused")
public class Nation extends BaseEntity<Nation> implements IPlayerCollective, ISerializable {
    @Getter Capital capital;
    @Getter List<String> towns;
    @Getter List<Resident> residents;
    @Getter String leader;
    @Getter Integer area;

    // Not exposed to serialization.
    private transient List<String> residentNames;

    /**
     * Creates a new Nation by parsing raw data.<br>
     * <font color="#e38c1b">Should <b>NOT</b> be called explicitly unless you know what you are doing!</font>
     * @param obj The unparsed data required to build this object.
     */
    public Nation(JsonObject obj) {
        super();
        init(obj);
    }

    private void init(JsonObject obj) {
        setInfo(this, keyAsStr(obj, "name"));

        leader = keyAsStr(obj, "king");
        area = keyAsInt(obj, "area");
        capital = new Capital(obj.getAsJsonObject("capital"));
        towns = Funcs.removeListDuplicates(toList(keyAsArr(obj, "towns")));

        JsonArray residentArr = keyAsArr(obj, "residents");
        residentNames = Funcs.removeListDuplicates(toList(residentArr));
        residents = Resident.fromArr(residentArr, "name");
    }

    // TODO: Finish invitableTowns
//    public Map<String, Town> invitableTowns(String mapName) {
//        Towns towns = Funcs.mapByName(mapName).Towns;
//
//        return collectAsMap(streamEntries(towns.all()).map(entry -> {
//            Town curTown = entry.getValue();
//        }));
//    }

    /**
     * Helper method to reduce mapping over {@link #residents} for names.
     * @return The names of residents in this nation.
     * @see #getResidents()
     */
    public List<String> residentList() {
        return residentNames;
    }

    /**
     * All residents that are online in this Nation.
     * @return A map of Residents with their entity {@link #name} being used as their respective keys.
     * @see #onlineResidents(List, BaseEntity)
     */
    public Map<String, Resident> onlineResidents() {
        return onlineResidents(residents, parent);
    }
}
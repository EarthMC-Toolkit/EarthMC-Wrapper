package io.github.emcw.map.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.emcw.KnownMap;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.interfaces.ISerializable;
import io.github.emcw.utils.Funcs;
import io.github.emcw.utils.GsonUtil;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static io.github.emcw.EMCWrapper.instance;
import static io.github.emcw.utils.Funcs.*;
import static io.github.emcw.utils.GsonUtil.*;

@SuppressWarnings("unused")
public class Nation extends BaseEntity<Nation> implements ISerializable {
    Capital capital;
    @Getter List<String> towns;
    @Getter List<Resident> residents;
    @Getter String leader;
    @Getter Integer area;

    // Not exposed to serialization.
    private transient List<String> residentNames;
    private final transient KnownMap map;

    /**
     * Creates a new Nation by parsing raw data.<br>
     * <font color="#e38c1b">Should <b>NOT</b> be called explicitly unless you know what you are doing!</font>
     * @param obj The unparsed data required to build this object.
     * @param map The map this nation currently resides in.
     */
    public Nation(JsonObject obj, KnownMap map) {
        super();

        this.map = map;
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

    public Town getCapital() {
        try {
            return instance().getMap(map).Towns.single(capital.getName());
        } catch (MissingEntryException e) {
            return new Town(capital);
        }
    }

    // TODO: Finish invitableTowns
    public Map<String, Town> invitableTowns() {
        Stream<Entry<String, Town>> towns = GsonUtil.streamEntries(instance().getMap(map).Towns.all());
        return collectEntities(towns.map(entry -> {
            Town town = entry.getValue();
            if (town.nation == null) {
                Location townLoc = town.getLocation();
                Location capitalLoc = getCapital().getLocation();

                // In range, return the town
                int inviteRange = map == KnownMap.AURORA ? 3500 : 3000;
                if (manhattan(capitalLoc, townLoc) < inviteRange) {
                    return town;
                }
            }

            return null;
        }));
    }

    /**
     * Helper method to reduce mapping over {@link #residents} for names.
     * @return The names of residents in this nation.
     * @see #getResidents()
     */
    public List<String> residentList() {
        return residentNames;
    }

}
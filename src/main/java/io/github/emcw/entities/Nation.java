package io.github.emcw.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.interfaces.IPlayerCollective;
import io.github.emcw.interfaces.ISerializable;
import io.github.emcw.map.Towns;
import io.github.emcw.utils.Funcs;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static io.github.emcw.utils.Funcs.collectEntities;
import static io.github.emcw.utils.GsonUtil.*;

@SuppressWarnings("unused")
public class Nation extends BaseEntity<Nation> implements IPlayerCollective, ISerializable {
    Capital capital;
    @Getter List<String> towns;
    @Getter List<Resident> residents;
    @Getter String leader;
    @Getter Integer area;

    // Not exposed to serialization.
    private transient List<String> residentNames;
    private final transient String mapName;

    /**
     * Creates a new Nation by parsing raw data.<br>
     * <font color="#e38c1b">Should <b>NOT</b> be called explicitly unless you know what you are doing!</font>
     * @param obj The unparsed data required to build this object.
     */
    public Nation(JsonObject obj, String mapName) {
        super();

        this.mapName = mapName;
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
        Towns towns = Funcs.mapByName(mapName).Towns;
        try {
            return towns.single(capital.getName());
        } catch (MissingEntryException e) {
            return new Town(capital);
        }
    }

    // TODO: Finish invitableTowns
    public Map<String, Town> invitableTowns(String mapName) {
        Towns towns = Funcs.mapByName(mapName).Towns;

        Stream<Entry<String, Town>> townsStream = streamEntries(towns.all());
        Stream<Town> townsMap = townsStream.map(entry -> {
            Town town = entry.getValue();

            Location townLoc = town.getLocation();
            Location capitalLoc = getCapital().getLocation();

            // In range, return the town
            if (Funcs.manhattan(capitalLoc, townLoc) < 2500) {
                return town;
            }

            // Otherwise null
            return null;
        });

        return collectEntities(townsMap);
    }

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
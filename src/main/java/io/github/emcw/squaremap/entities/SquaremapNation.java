package io.github.emcw.squaremap.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.emcw.KnownMap;
import io.github.emcw.Squaremap;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.interfaces.ISerializable;
import io.github.emcw.utils.Funcs;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static io.github.emcw.EMCWrapper.instance;
import static io.github.emcw.utils.GsonUtil.*;

@SuppressWarnings("unused")
public class SquaremapNation implements ISerializable {
    final transient String mapName; // Not exposed to serialization.

    @Getter String name;
    @Getter SquaremapCapital capital; // See getCapital()

    @Getter Set<String> towns;
    @Getter Set<String> residents;
    @Getter String leader;
    @Getter Integer area;

    /**
     * Creates a new Nation by parsing raw data.<br>
     * <font color="#e38c1b">Should <b>NOT</b> be called explicitly unless you know what you are doing!</font>
     * @param obj The unparsed data required to build this object.
     * @param map The map this nation currently resides in.
     */
    SquaremapNation(JsonObject obj, KnownMap map) {
        super();

        this.mapName = map.getName();
        init(obj);
    }

    private void init(JsonObject obj) {
        name = keyAsStr(obj, "name");

        leader = keyAsStr(obj, "king");
        area = keyAsInt(obj, "area");
        capital = new SquaremapCapital(obj.getAsJsonObject("capital"));
        //towns = Funcs.removeListDuplicates(toList(keyAsArr(obj, "towns")));

        String[] residentArr = keyAsStr(obj, "residents").split(", ");
        residents = Set.of(residentArr);
    }

    // Technically possible for this to throw, but shouldn't since the mapName was set given a KnownMap.
    Squaremap currentMap() throws IllegalArgumentException {
        KnownMap map = KnownMap.valueOf(mapName);
        return instance().getSquaremap(map);
    }

//    // TODO: Finish invitableTowns
//    public Map<String, SquaremapTown> invitableTowns() {
//        Stream<SquaremapTown> towns = streamEntries(currentMap().Towns.all()).map(entry -> {
//            SquaremapTown town = entry.getValue();
//            if (town.nation == null) {
//                SquaremapLocation townLoc = town.getLocation();
//                SquaremapLocation capitalLoc = getCapital().getLocation();
//
//                // In range, return the town
//                int inviteRange = map == KnownMap.AURORA ? 3500 : 3000;
//                if (manhattan(capitalLoc, townLoc) < inviteRange) {
//                    return town;
//                }
//            }
//
//            return null;
//        });
//
//        return towns.filter(Objects::nonNull).collect(
//            Collectors.toMap(SquaremapTown::getName, Function.identity())
//        );
//    }

//    /**
//     * Helper method to reduce mapping over {@link #residents} for names.
//     * @return The names of residents in this nation.
//     * @see #getResidents()
//     */
//    public List<String> residentList() {
//        return residentNames;
//    }
}
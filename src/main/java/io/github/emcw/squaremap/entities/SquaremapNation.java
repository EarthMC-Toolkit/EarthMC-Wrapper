package io.github.emcw.squaremap.entities;

import io.github.emcw.interfaces.IGsonSerializable;

import lombok.Getter;

import java.util.Set;

@SuppressWarnings("unused")
public class SquaremapNation implements IGsonSerializable {
    @Getter String name;
    @Getter SquaremapCapital capital; // See getCapital()

    @Getter Set<String> towns;
    @Getter Set<String> residents;
    @Getter Set<String> councillors;
    @Getter String leader; // King
    @Getter Integer area;

    /**
     * Creates a new Nation whose info should be subsequently set with {@link #updateInfo(SquaremapMarker, Set, Set)}.<br><br>
     * <font color="red">For internal use only!<br>
     * Explicit construction of this class is not advised unless you know what you are doing.
     * @param nationName The name of the name nation to create.</font>
     */
    public SquaremapNation(String nationName) {
        this.name = nationName;
    }

    // Add up area, determine capital (name, x, z) plus leader and wiki from said capital.
    public void updateInfo(SquaremapMarker marker, Set<String> residentNames, Set<String> councillorNames) {
        if (marker.townName != null) {
            this.towns.add(marker.townName);
        }

        this.residents.addAll(residentNames);
        this.councillors.addAll(councillorNames);

        this.area += marker.area;

        // Update leader
        if (marker.mayor != null) {
            this.leader = marker.mayor;
        }

        // Update capital
        if (marker.isCapital) {
            this.capital = new SquaremapCapital();
        }
    }

//    private void setFields(JsonObject obj) {
//        name = keyAsStr(obj, "name");
//
//        leader = keyAsStr(obj, "king");
//        area = keyAsInt(obj, "area");
//        capital = new SquaremapCapital(obj.getAsJsonObject("capital"));
//        //towns = Funcs.removeListDuplicates(toList(keyAsArr(obj, "towns")));
//
//        String[] residentArr = keyAsStr(obj, "residents").split(", ");
//        residents = Set.of(residentArr);
//    }

    // Technically possible for this to throw, but shouldn't since the mapName was set given a KnownMap.
//    Squaremap currentMap() throws IllegalArgumentException {
//        KnownMap map = KnownMap.valueOf(mapName);
//        return instance().getSquaremap(map);
//    }

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
package io.github.emcw.squaremap.entities;

import io.github.emcw.interfaces.IGsonSerializable;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unused", "LombokGetterMayBeUsed"})
public class SquaremapNation implements IGsonSerializable {
    @Getter String name;
    @Getter String leader; // King
    @Getter String wiki;

    @Getter int area;

    @Getter Set<String> towns = new HashSet<>();
    @Getter Set<String> residents = new HashSet<>();
    @Getter Set<String> councillors = new HashSet<>();

    @Getter SquaremapCapital capital; // See getCapital()

    /**
     * Creates a new Nation whose info should be subsequently set with {@link #updateInfo(SquaremapMarker, Set, Set)}.<br><br>
     * <font color="red">For internal use only!<br>
     * Explicit construction of this class is not advised unless you know what you are doing.
     * @param nationName The name of the name nation to create.</font>
     */
    public SquaremapNation(String nationName) {
        this.name = nationName;
    }

    public void updateInfo(SquaremapMarker marker, Set<String> residentNames, Set<String> councillorNames) {
        this.area += marker.area;

        this.residents.addAll(residentNames);
        this.councillors.addAll(councillorNames);

        if (marker.townName != null) {
            this.towns.add(marker.townName);
        }

        if (marker.isCapital) {
            this.capital = new SquaremapCapital(marker);
            this.leader = marker.mayor;
            this.wiki = marker.nationWiki;
        }
    }

//    /**
//     * Helper method to reduce mapping over {@link #residents} for names.
//     * @return The names of residents in this nation.
//     * @see #getResidents()
//     */
//    public List<String> residentList() {
//        return residentNames;
//    }
}
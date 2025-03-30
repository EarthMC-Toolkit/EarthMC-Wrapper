package io.github.emcw.squaremap.entities;

import io.github.emcw.interfaces.IGsonSerializable;

import lombok.Getter;

@Getter
@SuppressWarnings("unused")
public class SquaremapResident implements IGsonSerializable {
    private final String name, town, nation, rank;

    public SquaremapResident(String resName, SquaremapMarker marker, boolean isCouncillor) {
        this.name = resName;
        this.town = marker.townName;
        this.nation = marker.nationName;

        boolean isMayor = marker.mayor.equals(getName());

        String rank = isCouncillor ? "Councillor" : "Resident";
        this.rank = isMayor ? (marker.isCapital ? "Nation Leader" : "Mayor") : rank;
    }

    public boolean isCouncillor() {
        return this.rank.equals("Councillor");
    }

    public boolean isMayor() {
        return this.rank.equals("Mayor");
    }

    public boolean isNationLeader() {
        return this.rank.equals("Nation Leader");
    }

    /**
     * <p>Checks whether this resident has more permissions than a regular resident.</p>
     * @return true if {@link #rank} is a {@code Mayor}, {@code Nation Leader} or {@code Councillor} - false otherwise.
     */
    public boolean hasAuthority() {
        return isCouncillor() || isMayor() || isNationLeader();
    }
}
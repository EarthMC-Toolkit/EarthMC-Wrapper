package io.github.emcw.squaremap.entities;

import io.github.emcw.interfaces.IGsonSerializable;

import lombok.Getter;

@SuppressWarnings("unused")
public class SquaremapResident implements IGsonSerializable {
    @Getter private final String name, town, nation, rank;

    public SquaremapResident(String resName, SquaremapMarker marker, boolean isCouncillor) {
        this.name = resName;
        this.town = marker.townName;
        this.nation = marker.nationName;

        boolean isMayor = marker.mayor.equals(getName());
        this.rank = isMayor ? (marker.isCapital ? "Nation Leader" : "Mayor") : "Resident";
    }

    public boolean isMayor() {
        return this.rank.equals("Mayor");
    }

    public boolean isNationLeader() {
        return this.rank.equals("Nation Leader");
    }

    public boolean isCouncillor() {
        return this.rank.equals("Councillor");
    }

    /**
     * <p>Checks whether this resident has more permissions than a regular resident.</p>
     * @return true if {@link #rank} is a {@code Mayor}, {@code Nation Leader} or {@code Councillor} - false otherwise.
     */
    public boolean hasAuthority() {
        return isNationLeader() || isCouncillor() || isMayor();
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
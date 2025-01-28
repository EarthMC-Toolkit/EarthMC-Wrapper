package io.github.emcw.squaremap.entities;

import io.github.emcw.interfaces.IGsonSerializable;
import com.google.gson.JsonObject;

import lombok.Getter;

@SuppressWarnings("unused")
public class SquaremapOnlineResident extends SquaremapOnlinePlayer implements IGsonSerializable {
    @Getter private final String town, nation, rank;

    public SquaremapOnlineResident(JsonObject opInfo, SquaremapResident res) {
        super(opInfo);
        this.town = res.getTown();
        this.nation = res.getNation();
        this.rank = res.getRank();
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
}
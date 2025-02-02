package io.github.emcw.squaremap.entities;

import io.github.emcw.interfaces.IGsonSerializable;

import lombok.Getter;

@SuppressWarnings({"unused", "LombokGetterMayBeUsed"})
public class SquaremapOnlineResident extends SquaremapOnlinePlayer implements IGsonSerializable {
    @Getter private String town;
    @Getter private String nation;
    @Getter private String rank;

    public SquaremapOnlineResident(SquaremapOnlinePlayer op, SquaremapResident res) {
        super(op);

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
package net.emc.emcw.objects;

import net.emc.emcw.utils.Pair;

import java.util.List;

public class Nation {
    Capital capital;
    List<Town> towns;

    String name, leader;
    Integer area;

    Nation(String name, String leader, Integer area, List<Town> towns, Capital capital) {
        this.name = name;
        this.capital = capital;
        this.leader = leader;
        this.area = area;
        this.towns = towns;
    }

    public static Nation fromTowns(List<Town> towns) {
        return null;
    }
}

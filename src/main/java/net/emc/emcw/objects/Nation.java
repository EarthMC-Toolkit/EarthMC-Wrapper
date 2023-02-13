package net.emc.emcw.objects;

import net.emc.emcw.utils.Pair;

import java.util.List;

public class Nation {
    Pair<Integer, Integer> location;
    List<Town> towns;

    String name, capital, leader;
    Integer area;

    Nation(String name, String capital, String leader, Integer area,
           List<Town> towns, Pair<Integer, Integer> location)
    {
        this.name = name;
        this.capital = capital;
        this.leader = leader;
        this.area = area;
        this.towns = towns;
        this.location = location;
    }

    public static Nation fromTowns(List<Town> towns) {
        return null;
    }
}

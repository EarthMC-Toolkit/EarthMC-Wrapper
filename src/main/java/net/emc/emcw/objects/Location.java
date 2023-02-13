package net.emc.emcw.objects;

import static java.lang.Integer.parseInt;

public class Location {
    public final Integer x, y;

    Location(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    Location(String x, String y) {
        this.x = parseInt(x);
        this.y = parseInt(y);
    }
}
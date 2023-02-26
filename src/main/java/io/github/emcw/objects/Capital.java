package io.github.emcw.objects;

public class Capital {
    public final String name;
    public final Location location;

    Capital(String capitalName, Location loc) {
        this.name = capitalName;
        this.location = loc;
    }
}
package io.github.emcw;

import java.io.Serializable;

@SuppressWarnings("unused")
public enum Direction implements Serializable {
    NORTH("North"),
    EAST("East"),
    SOUTH("South"),
    WEST("West"),
    NORTHWEST("North-west"),
    NORTHEAST("North-east"),
    SOUTHWEST("South-west"),
    SOUTHEAST("South-east");

    private final String value;

    Direction(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public boolean matches(String direction) {
        return this.value.equalsIgnoreCase(direction);
    }
}
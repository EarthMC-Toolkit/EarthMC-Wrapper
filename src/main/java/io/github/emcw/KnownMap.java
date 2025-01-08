package io.github.emcw;

import lombok.Getter;

public enum KnownMap {
    AURORA("aurora");

    @Getter private final String name;

    KnownMap(String name) {
        this.name = name;
    }
}
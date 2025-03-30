package io.github.emcw;

import lombok.Getter;

@Getter
public enum KnownMap {
    AURORA("aurora");

    private final String name;

    KnownMap(String name) {
        this.name = name;
    }
}
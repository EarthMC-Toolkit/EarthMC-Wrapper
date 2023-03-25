package io.github.emcw.entities;

import lombok.Getter;

public abstract class BaseEntity<T> {
    @Getter String name;
    @Getter T parent;

    protected void setInfo(T parent, String name) {
        this.parent = parent;
        this.name = name;
    }
}
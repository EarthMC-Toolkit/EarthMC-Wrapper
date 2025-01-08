package io.github.emcw.map.entities;

import lombok.Getter;

/**
 * A generic base class that all other entities inherit from. Holds a name and a reference to its parent.
 * @param <T> Determines the class type of the {@link #parent} reference.
 */
@Getter
public abstract class BaseEntity<T> {
    String name;
    T parent;

    protected void setInfo(T parent, String name) {
        this.parent = parent;
        this.name = name;
    }
}
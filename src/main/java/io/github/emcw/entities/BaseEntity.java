package io.github.emcw.entities;

import lombok.Getter;

/**
 * <p>An generic base class that all other entities inherit from. Holds a name and a reference to its parent.</p>
 * @param <T> Determines the class type of the {@link #parent} reference.
 */
public abstract class BaseEntity<T> {
    @Getter String name;
    @Getter T parent;

    protected void setInfo(T parent, String name) {
        this.parent = parent;
        this.name = name;
    }
}
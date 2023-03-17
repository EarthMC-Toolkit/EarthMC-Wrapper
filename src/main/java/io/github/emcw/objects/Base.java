package io.github.emcw.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class Base<T> {
    @Getter String name;
    @Getter T parent;

    protected void setInfo(T parent, String name) {
        this.parent = parent;
        this.name = name;
    }
}
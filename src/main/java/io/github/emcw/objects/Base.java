package io.github.emcw.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Base<T> {
    @Getter String name;
    @Getter T parent;

    void setInfo(T parent, String name) {
        this.parent = parent;
        this.name = name;
    }
}

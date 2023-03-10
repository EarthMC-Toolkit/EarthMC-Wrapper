package io.github.emcw.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Base<T> {
    @Getter protected String name;
    @Getter protected T value;

    void setInfo(T parent, String name) {
        this.value = parent;
        this.name = name;
    }
}

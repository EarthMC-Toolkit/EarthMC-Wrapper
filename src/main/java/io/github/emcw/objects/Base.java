package io.github.emcw.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Base<T> {
    @Getter String name;
    @Getter T value;

    void setInfo(T parent, String name) {
        this.value = parent;
        this.name = name;
    }
}

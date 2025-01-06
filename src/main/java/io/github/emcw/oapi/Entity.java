package io.github.emcw.oapi;

import io.github.emcw.interfaces.ISerializable;
import org.jetbrains.annotations.Nullable;

public class Entity implements ISerializable {
    @Nullable public final String name;
    @Nullable public final String uuid; // TODO: Use `UUID` type instead to do validation.

    public Entity(@Nullable String name, @Nullable String uuid) {
        this.name = name;
        this.uuid = uuid;
    }
}
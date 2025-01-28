package io.github.emcw.common;

import io.github.emcw.interfaces.ISerializable;

import org.jetbrains.annotations.Nullable;
import lombok.Getter;

import java.util.UUID;

public class Entity implements ISerializable {
    @Getter @Nullable
    protected final UUID uuid; // TODO: Use `UUID` type instead to do validation.

    @Getter @Nullable
    protected final String name;

    public Entity(@Nullable String uuid, @Nullable String name) {
        this.uuid = uuid == null ? null : UUID.fromString(uuid);
        this.name = name;
    }
}
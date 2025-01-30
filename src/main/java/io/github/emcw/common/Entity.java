package io.github.emcw.common;

import io.github.emcw.interfaces.IGsonSerializable;
import io.github.emcw.utils.Funcs;

import org.jetbrains.annotations.Nullable;
import lombok.Getter;

import java.util.UUID;

@SuppressWarnings("LombokGetterMayBeUsed")
public class Entity implements IGsonSerializable {
    @Getter @Nullable
    protected final UUID uuid;

    @Getter @Nullable
    protected final String name;

    public Entity(@Nullable String uuid, @Nullable String name) {
        this.uuid = uuid == null ? null : Funcs.stringToFullUUID(uuid);
        this.name = name;
    }
}
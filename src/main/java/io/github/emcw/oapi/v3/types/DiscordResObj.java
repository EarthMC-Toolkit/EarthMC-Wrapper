package io.github.emcw.oapi.v3.types;

import io.github.emcw.interfaces.IGsonSerializable;
import org.jetbrains.annotations.Nullable;

public record DiscordResObj(@Nullable String id, @Nullable String uuid) implements IGsonSerializable { }
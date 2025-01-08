package io.github.emcw.oapi.v3.types;

import io.github.emcw.interfaces.ISerializable;
import org.jetbrains.annotations.Nullable;

public record DiscordResObj(@Nullable String id, @Nullable String uuid) implements ISerializable { }
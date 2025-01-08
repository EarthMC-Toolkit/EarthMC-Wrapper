package io.github.emcw.oapi.v3.types;

import io.github.emcw.interfaces.ISerializable;

public record DiscordReqObj(String type, String target) implements ISerializable { }
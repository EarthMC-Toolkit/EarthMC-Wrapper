package io.github.emcw.oapi.v3.types;

import io.github.emcw.interfaces.IGsonSerializable;

public record DiscordReqObj(String type, String target) implements IGsonSerializable { }
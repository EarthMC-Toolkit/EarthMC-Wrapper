package io.github.emcw.enums;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public enum CacheStrategy {
    NONE,
    LAZY,
    TIME_BASED
}
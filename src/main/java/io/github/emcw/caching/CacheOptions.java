package io.github.emcw.caching;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class CacheOptions {
    long expiry;
    TimeUnit unit;

    final CacheStrategy strategy;

    /**
     * Creates a new options instance that will be passed when building a cache.<br><br>
     * If {@code strategy} is {@link CacheStrategy#TIME_BASED}, the cache will automatically update after the duration,
     * otherwise it must be {@link CacheStrategy#LAZY} and will be emptied until
     * @param strategy The strategy for the cache to use when updating.
     * @param duration Amount of time until strategy-dependenant logic happens.
     * @param unit Time unit to base duration on. Min, sec, ms etc.
     */
    public CacheOptions(CacheStrategy strategy, long duration, @NotNull TimeUnit unit) {
        this.strategy = strategy;
        this.expiry = duration;
        this.unit = unit;
    }

    Duration expiryDuration() {
        return Duration.of(this.expiry, this.unit.toChronoUnit());
    }
}
package io.github.emcw.caching;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class CacheOptions {
    long expiry;
    TimeUnit unit;

    final CacheStrategy strategy;

//    public CacheOptions(CacheStrategy strategy) {
//        this.strategy = strategy;
//    }
//
//    public CacheOptions(long expireAfterWrite, @NotNull TimeUnit unit) {
//        this.expiry = expireAfterWrite;
//        this.unit = unit;
//    }

    public CacheOptions(CacheStrategy strategy, long expireAfterWrite, @NotNull TimeUnit unit) {
        this.strategy = strategy;
        this.expiry = expireAfterWrite;
        this.unit = unit;
    }

    @SuppressWarnings("unused")
    Duration expiryAsDuration(long time, @NotNull TimeUnit unit) {
        return Duration.of(time, unit.toChronoUnit());
    }
}
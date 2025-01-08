package io.github.emcw.caching;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Setter
@SuppressWarnings("unused")
public class CacheOptions {
    public long expiry = 3;
    public TimeUnit unit = TimeUnit.MINUTES;

    public CacheStrategy strategy = CacheStrategy.TIME_BASED;

    public CacheOptions(CacheStrategy strategy) {
        setStrategy(strategy);
    }

    public CacheOptions(long expireAfterWrite, @NotNull TimeUnit unit) {
        setExpiry(expireAfterWrite);
        setUnit(unit);
    }

    public CacheOptions(long expireAfterWrite, @NotNull TimeUnit unit, CacheStrategy strategy) {
        setExpiry(expireAfterWrite);
        setUnit(unit);

        setStrategy(strategy);
    }

    Duration expiryAsDuration(long time, @NotNull TimeUnit unit) {
        return Duration.of(time, unit.toChronoUnit());
    }
}
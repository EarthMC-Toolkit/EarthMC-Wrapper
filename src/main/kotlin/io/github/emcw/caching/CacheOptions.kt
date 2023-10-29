package io.github.emcw.caching

import lombok.Setter
import java.time.Duration
import java.util.concurrent.TimeUnit

class CacheOptions {
    @Setter
    var expiry: Long = 3

    @Setter
    var unit = TimeUnit.MINUTES

    @Setter
    var strategy = CacheStrategy.TIME_BASED

    constructor(strategy: CacheStrategy?) {
        setStrategy(strategy)
    }

    constructor(expireAfterWrite: Long, unit: TimeUnit) {
        setExpiry(expireAfterWrite)
        setUnit(unit)
    }

    constructor(expireAfterWrite: Long, unit: TimeUnit, strategy: CacheStrategy?) {
        setExpiry(expireAfterWrite)
        setUnit(unit)
        setStrategy(strategy)
    }

    fun expiryAsDuration(time: Long, unit: TimeUnit): Duration {
        return Duration.of(time, unit.toChronoUnit())
    }
}
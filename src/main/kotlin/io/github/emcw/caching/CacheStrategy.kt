package io.github.emcw.caching

/**
 * Specifies the caching strategy to use.****
 *
 * **Items: ** [.LAZY], [.TIME_BASED], [.HYBRID]
 */
enum class CacheStrategy {
    /**
     * Only update on next call (expiry should be set)
     */
    LAZY,

    /**
     * Always update at an interval (expiry not needed)
     */
    TIME_BASED,

    /**
     * Time based, but forces update if called before interval.
     */
    HYBRID
}
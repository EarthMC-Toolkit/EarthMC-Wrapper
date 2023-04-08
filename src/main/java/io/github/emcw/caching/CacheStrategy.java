package io.github.emcw.caching;

/**
 * Specifies the caching strategy to use.<b></b>
 * <p><b>Items: </b> {@link #LAZY}, {@link #TIME_BASED}, {@link #HYBRID}
 */
public enum CacheStrategy {
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
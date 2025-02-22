package io.github.emcw.caching;

/**
 * Specifies the caching strategy to use.<b></b>
 * <p><b>Types: </b> {@link #LAZY}, {@link #TIME_BASED}
 */
public enum CacheStrategy {
    /**
     * Only update on next call if expired (expiry should be set).
     */
    LAZY,
    /**
     * Always update at an interval (expiry not needed).
     */
    TIME_BASED
}
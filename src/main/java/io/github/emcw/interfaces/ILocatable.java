package io.github.emcw.interfaces;

import java.util.Map;

public interface ILocatable<T> {
    default Map<String, T> nearby(Map<String, T> ops) {

        return null;
    }
}

package io.github.emcw.interfaces;

import org.jetbrains.annotations.NotNull;

import java.util.List;

// Unsure if needed, but left here for now.
public interface IMapParser {
    void parsePlayerData();
    void parseMapData(Boolean parseTowns, Boolean parseNations, Boolean parseResidents);
    void parseResidents();
    void parseTowns();
    void parseNations();

    /**
     * Given the list of flags (string in the format <code>"Key: boolean"</code>), get the flag at the
     * <code>index</code> and extract the value of the given <code>key</code> as a boolean.
     * @param kbStrings The list of flag strings.
     * @param index The index of the list we want the flag string from.
     * @param key The substring to slice off. Example: <code>"PVP: "</code>
     * @return True if the key was true, False if the key was false or not a boolean.
     */
    default boolean flagAsBool(@NotNull List<String> kbStrings, Integer index, String key) {
        String str = kbStrings.get(index).replace(key, "");
        return Boolean.parseBoolean(str);
    }
}
package me.athlaeos.lapi.placeholder;

import me.athlaeos.lapi.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Undefined placeholders are placeholders that don't use a static placeholder name
 */
public interface ParameterizedPlaceholder {
    /**
     * The identifier of the placeholder. Should unique and not match any other expansions, unless
     * you want to overwrite them. It should also not contain a segment of another expansion identifier. <br>
     * For example, giving your expansion the "coolplugin" identifier might clash with another
     * expansion called "coolpluginradical". <br>
     * Should conventionally match the plugin's name and must consist of
     * only lowercase letters and/or underscores
     */
    @NotNull String getIdentifier();

    /**
     * Parses a placeholder and returns a string. If null is returned, the placeholder is left
     * alone. A placeholder will follow the format of "$<code>getIdentifier()</code>_params$".
     * @param player The reference player
     * @param item The reference item
     * @param params The parameter being parsed. Example: $lapi_item_name$ produces a <code>params</code> of item_name
     * @return The parsed string, or null if you want the placeholder ignored
     */
    default String parseString(Player player, ItemBuilder item, @NotNull String params){
        return null;
    }

    /**
     * Parses a placeholder and returns a list of strings. if null is returned, the placeholder is
     * left alone. A placeholder will follow the format of "$<code>getIdentifier()</code>_params$".<br>
     * A list placeholder is inserted into the line it's found in, much like {@link ListPlaceholder}.
     * @param player The reference player
     * @param item The reference item
     * @param params The parameter being parsed. Example: $lapi_item_lore$ produces a <code>params</code> of item_lore
     * @return The parsed string, or null if you want the placeholder ignored
     */
    default List<String> parseList(Player player, ItemBuilder item, @NotNull String params){
        return null;
    }

    default void register(){
        PlaceholderRegistry.registerPlaceholder(this);
    }
}

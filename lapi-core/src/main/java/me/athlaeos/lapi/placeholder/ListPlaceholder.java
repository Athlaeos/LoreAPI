package me.athlaeos.lapi.placeholder;

import me.athlaeos.lapi.utils.ItemBuilder;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class ListPlaceholder implements StaticPlaceholder {
    /**
     * ListPlaceholders work a little different to StringPlaceholders. Instead of replacing a
     * string, they insert a list in the line where the placeholder used to be. <br>
     * Whatever text was before and after the placeholder is used as prefix and suffix
     * of every line returned by the placeholder.<br>
     * <b>Only 1 list placeholder may be used per line, any additional placeholders are discarded</b><br>
     * <br>
     * As example, we have a list with the following entries: <br>
     * - <code>&fLine numero uno</code><br>
     * - <code>&fPrefix %lapi_lore% Suffix</code><br>
     * - <code>&fLine numero cuatro</code><br>
     * <br>
     * The placeholder %lapi_lore% returns the following list:<br>
     * - <code>dos</code><br>
     * - <code>tres</code><br>
     * <br>
     * The resulting list after placeholder replacement looks like so:<br>
     * - <code>&fLine numero uno</code><br>
     * - <code>&fPrefix dos Suffix</code><br>
     * - <code>&fPrefix tres Suffix</code><br>
     * - <code>&fLine numero cuatro</code><br>
     *<br>
     * The ItemBuilder represents the item in an inventory. Its intended purpose is to provide
     * an easy interface through which to access the item's properties, but it may also be modified
     * to change the appearance of the item further (such as model, tooltip, etc.). <br>
     * It is not recommended to change the display name or lore directly through this ItemBuilder.<br><br>
     * Keep in mind changes made to the item will not actually make any changes to the item's meta,
     * they are only visual. If you add enchantments to the item, the player using the item
     * will have no benefit of it. Likewise, removing enchantments while the item has
     * enchantments will still allow the player to benefit off of these enchantments, despite not
     * visually appearing.<br><br>
     * If you solely intend for a placeholder to make visual changes to the item without affecting
     * lore, you may make your changes using a ListPlaceholder and have it return an empty list.
     * The placeholder must still be present in the item's lore format, but it will disappear after
     * replacing. <br><br>
     * For obvious reasons, list placeholders are not applied in the item's display name
     * @param player The player seeing the item
     * @param item The item being seen
     * @return The string list to replace the placeholder with
     */
    public abstract List<String> parse(Player player, ItemBuilder item);
}

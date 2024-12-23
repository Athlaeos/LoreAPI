package me.athlaeos.lapi.placeholder;

import me.athlaeos.lapi.utils.ItemBuilder;
import org.bukkit.entity.Player;

public abstract class StringPlaceholder implements StaticPlaceholder {
    /**
     * Parses a string for any placeholders and inserts the returned string in place of it. <br>
     * For example, <code>&fDurability: $lapi_item_durability$</code> will be parsed to <code>&fDurability: 420</code>
     * assuming the item has 420 durability remaining. These placeholders should only really
     * be made if they utilize an item's properties at all, otherwise you might as well use
     * PlaceholderAPI.
     * The ItemBuilder represents the item in an inventory. Its intended purpose is to provide
     * an easy interface through which to access the item's properties, but it may also be modified
     * to change the appearance of the item further (such as model, tooltip, etc.). <br>
     * It is not recommended to change the display name or lore directly through this ItemBuilder.<br><br>
     * Keep in mind changes made to the item will not actually make any changes to the item's meta,
     * they are only visual. If you add enchantments to the item, the player using the item
     * will have no benefit of it. Likewise, removing enchantments while the item has
     * enchantments will still allow the player to benefit off of these enchantments, despite not
     * visually appearing.
     * @param player The player seeing the item
     * @param item The item being seen
     * @return The string to replace the placeholder with
     */
    public abstract String parse(Player player, ItemBuilder item);
}

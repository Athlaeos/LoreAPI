package me.athlaeos.lapi;

import me.athlaeos.lapi.format.DisplayFormat;
import me.athlaeos.lapi.format.DisplayFormatRegistry;
import me.athlaeos.lapi.placeholder.*;
import me.athlaeos.lapi.utils.ItemBuilder;
import me.athlaeos.lapi.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoreAPI {
    /**
     * Produces a format-compliant ItemStack given the player observing the item and the item itself. <br>
     * If the item has a format in its NBT data, it is used. If not, its material default format is used.
     * If no material default format was defined, the global default is used. If that isn't defined either, well,
     * I guess this item is just returned as-is.
     * @param observer The player observing the item
     * @param item The item being observed
     * @return The item after a display format is enforced on it
     */
    public static ItemStack parseItemStack(Player observer, ItemStack item){
        return PlaceholderRegistry.parseItemStack(item, observer);
    }

    /**
     * Replaces any StringPlaceholders in the string according to a player observer and an ItemStack.
     * Due to this string, in fact, not being a list, ListPlaceholders are ignored. If you want to parse
     * a list, use parseStringList(Player, ItemStack, List<String>) instead. <br><br>
     * See {@link StringPlaceholder#parse(Player, ItemBuilder)} for more info on how strings are treated.
     * @param observer The player reference, usually the one observing the string
     * @param item The item reference, usually the item being observed
     * @param string The string to parse
     * @return The parsed string
     */
    public static String parseString(Player observer, ItemStack item, String string){
        if (ItemUtils.isEmpty(item)) throw new IllegalArgumentException("Item must not be null or air");
        return PlaceholderRegistry.parseString(observer, new ItemBuilder(item), string);
    }

    /**
     * Replaces any Placeholders in the list according to a player observer and an ItemStack. <br><br>
     * See {@link ListPlaceholder#parse(Player, ItemBuilder)} for more info on how lists are treated.
     * @param observer The player reference, usually the one observing the item
     * @param item The item reference, usually the item being observed
     * @param stringList The list of strings to parse
     * @return The parsed string list
     */
    public static List<String> parseStringList(Player observer, ItemStack item, List<String> stringList){
        if (ItemUtils.isEmpty(item)) throw new IllegalArgumentException("Item must not be null or air");
        return PlaceholderRegistry.parseList(observer, new ItemBuilder(item), stringList);
    }

    /**
     * Registers a placeholder to be used by LoreAPI. Must extend either {@link StringPlaceholder} or {@link ListPlaceholder}.<br>
     * {@link StaticPlaceholder#register()} works too. In fact, that's just what this method does. Re-registration of
     * placeholders may reset some of its configurations.
     * @param placeholder The placeholder to register
     */
    public static void registerPlaceholder(StaticPlaceholder placeholder){
        placeholder.register();
    }

    /**
     * Registers a placeholder to be used by LoreAPI.<br>
     * {@link ParameterizedPlaceholder#register()} works too. In fact, that's just what this method does. Re-registration of
     * placeholders may reset some of its configurations.<br>
     * Unlike {@link StaticPlaceholder}, ParameterizedPlaceholders are able to have the placeholder text
     * changed which is passed as argument to {@link ParameterizedPlaceholder#parseString(Player, ItemBuilder, String)} and
     * {@link ParameterizedPlaceholder#parseList(Player, ItemBuilder, String)}. <br>
     * This placeholder is heavier on performance (not by a lot) and you'll generally want to prefer using
     * StaticPlaceholders instead.
     * @param placeholder The placeholder to register
     */
    public static void registerParameterizedPlaceholder(ParameterizedPlaceholder placeholder){
        placeholder.register();
    }

    /**
     * Returns an unmodifiable map containing all registered placeholders
     * @return An unmodifiable map of all registered placeholders
     */
    public static Map<String, StaticPlaceholder> getRegisteredPlaceholders(){
        return PlaceholderRegistry.getStaticPlaceholders();
    }

    /**
     * Returns an unmodifiable map containing all registered parameterized placeholders
     * @return An unmodifiable map of all registered placeholders
     */
    public static Map<String, ParameterizedPlaceholder> getRegisteredParameterizedPlaceholders(){
        return PlaceholderRegistry.getParameterizedPlaceholders();
    }

    /**
     * Registers a DisplayFormat to be registered to LoreAPI. DisplayFormats are usually defined in
     * formats.yml, but you may use this method to add custom implementations of DisplayFormat.
     * @param format The format to register
     */
    public static void registerDisplayFormat(DisplayFormat format){
        DisplayFormatRegistry.registerFormat(format);
    }

    /**
     * Returns an unmodifiable map containing all registered display formats
     * @return An unmodifiable map of all registered display formats
     */
    public static Map<String, DisplayFormat> getRegisteredDisplayFormats(){
        return DisplayFormatRegistry.getRegisteredFormats();
    }

    /**
     * Updates the player's inventory, updating all the items in it with updated fake appearances
     * @param player The player to update their open inventory view for
     */
    @SuppressWarnings("UnstableApiUsage")
    public static void sendInventoryUpdate(Player player){
        player.updateInventory();
        InventoryView openView = player.getOpenInventory();
        for (int i = 0; i < openView.countSlots(); i++){
            ItemStack inSlot = openView.getItem(i);
            if (ItemUtils.isEmpty(inSlot)) continue;
            int containerID = LoreAPIPlugin.getNms().getOpenContainerID(player);
            int state = LoreAPIPlugin.getNms().getOpenContainerState(player);
            if (containerID < 0 || state < 0) continue;
            LoreAPIPlugin.getNms().sendContainerItem(player, inSlot, i, containerID, state);
        }
    }

    /**
     * Updates a player's open inventory view, at one slot specifically.
     * @param player The player to update their inventory view
     * @param slot The slot index to update
     * @return The UpdateStatus. <br>
     * If no item was found in the given slot, <code>NO_ITEM_IN_SLOT</code> is returned<br>
     * If the player has no container open, <code>INVALID_CONTAINER_ID</code> is returned<br>
     * If the player's open container has no valid state, <code>INVALID_CONTAINER_STATE</code> is returned<br>
     * If all goes well, <code>SUCCESS</code> is returned.
     */
    public static UpdateStatus sendContainerSlotUpdate(Player player, int slot){
        InventoryView openView = player.getOpenInventory();
        ItemStack inSlot = openView.getItem(slot);
        if (ItemUtils.isEmpty(inSlot)) return UpdateStatus.NO_ITEM_IN_SLOT;
        int containerID = LoreAPIPlugin.getNms().getOpenContainerID(player);
        int state = LoreAPIPlugin.getNms().getOpenContainerState(player);
        if (containerID < 0) return UpdateStatus.INVALID_CONTAINER_ID;
        if (state < 0) return UpdateStatus.INVALID_STATE_ID;
        LoreAPIPlugin.getNms().sendContainerItem(player, inSlot, slot, containerID, state);
        return UpdateStatus.SUCCESS;
    }

    /**
     * Visually replaces the player's open inventory view with the given items.
     * @param player The player observer
     * @param items The items to replace the inventory view contents with ((visually))
     * @return The UpdateStatus. <br>
     * If the player has no container open, <code>INVALID_CONTAINER_ID</code> is returned<br>
     * If the player's open container has no valid state, <code>INVALID_CONTAINER_STATE</code> is returned<br>
     * If all goes well, <code>SUCCESS</code> is returned.
     */
    public static UpdateStatus sendContainerContents(Player player, List<ItemStack> items){
        int containerID = LoreAPIPlugin.getNms().getOpenContainerID(player);
        int state = LoreAPIPlugin.getNms().getOpenContainerState(player);
        if (containerID < 0) return UpdateStatus.INVALID_CONTAINER_ID;
        if (state < 0) return UpdateStatus.INVALID_STATE_ID;
        LoreAPIPlugin.getNms().sendContainerContents(player, items, containerID, state);
        return UpdateStatus.SUCCESS;
    }

    /**
     * Visually replaces the given player's inventory slot with the given item.
     * @param player The player observer
     * @param slot The slot to replace the item of ((visually))
     * @param item The item to insert in the slot ((((visually))))
     */
    public static void sendPlayerSlot(Player player, int slot, ItemStack item){
        LoreAPIPlugin.getNms().sendPlayerItem(player, item, slot);
    }

    public enum UpdateStatus{
        NO_ITEM_IN_SLOT,
        INVALID_CONTAINER_ID,
        INVALID_STATE_ID,
        SUCCESS
    }
}

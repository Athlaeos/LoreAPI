package me.athlaeos.lapi.utils;

import me.athlaeos.lapi.LoreAPIPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ItemUtils {
    public static void addItem(Player player, ItemStack i, boolean setOwnership){
        Map<Integer, ItemStack> excess = player.getInventory().addItem(i);
        if (!excess.isEmpty()){
            for (Integer slot : excess.keySet()){
                ItemStack slotItem = excess.get(slot);
                Item drop = player.getWorld().dropItem(player.getLocation(), slotItem);
                if (setOwnership) drop.setOwner(player.getUniqueId());
            }
        }
    }

    public static List<ItemStack> decompressStacks(Map<ItemStack, Integer> contents){
        List<ItemStack> listedItems = new ArrayList<>();
        for (ItemStack i : contents.keySet()){
            int amount = contents.get(i);
            int limiter = 54;
            do {
                ItemStack copy = i.clone();
                copy.setAmount(Math.min(amount, i.getMaxStackSize()));
                listedItems.add(copy);
                amount -= copy.getAmount();
                limiter--;
            } while (amount > 0 && limiter > 0);
        }
        return listedItems;
    }

    public static Map<ItemStack, Integer> compressStacks(List<ItemStack> items){
        Map<ItemStack, Integer> contents = new HashMap<>();
        for (ItemStack i : items){
            if (ItemUtils.isEmpty(i)) continue;
            ItemStack clone = i.clone();
            int itemAmount = clone.getAmount();
            clone.setAmount(1);
            if (contents.containsKey(clone)){
                contents.put(clone, contents.get(clone) + itemAmount);
            } else {
                contents.put(clone, itemAmount);
            }
        }
        return contents;
    }

    public static void replaceOrAddLore(ItemMeta meta, String find, String replacement){
        find = ChatColor.stripColor(Utils.chat(find));
        if (meta == null) return;
        List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
        replaceOrAddLore(lore, find, replacement);
        meta.setLore(lore);
    }

    public static void replaceOrAddLore(List<String> original, String find, String replacement){
        find = ChatColor.stripColor(Utils.chat(find));
        if (original == null) return;
        int index = -1;
        for (String l : original){
            if (l.contains(find)){
                index = original.indexOf(l);
                break;
            }
        }

        if (index != -1) {
            // match found
            if (StringUtils.isEmpty(replacement)) original.remove(index);
            else original.set(index, Utils.chat(replacement));
        } else {
            // no match found
            if (!StringUtils.isEmpty(replacement))
                original.add(Utils.chat(replacement));
        }
    }

    public static boolean isEmpty(ItemStack i){
        return i == null || i.getType().isAir() || i.getAmount() <= 0;
    }

    private static final NamespacedKey TYPE_KEY = new NamespacedKey(LoreAPIPlugin.getInstance(), "temporary_type_storage");

    /**
     * Wrapper to get the item meta of the item. The type of the item is immediately stored as temporary variable onto the meta.
     * If the meta is to be returned to the item, {@link ItemUtils#setItemMeta(ItemStack, ItemMeta)} is expected to be used.
     * It's not a big deal if it's not used, you just have a jump nbt tag left on the item.
     * @param i the item to get the item meta from
     * @return the item meta, if any. Null if the item is null or air or if the returned meta is also null.
     */
    public static ItemMeta getItemMeta(ItemStack i){
        if (isEmpty(i)) return null;
        ItemMeta meta = i.getItemMeta();
        if (meta == null) return null;
        meta.getPersistentDataContainer().set(TYPE_KEY, PersistentDataType.STRING, i.getType().toString());
        return meta;
    }

    /**
     * Sets the item meta to the item, removing the temporary type variable from the meta first.
     * @param i the item to set the item meta to
     * @param meta the item meta to put on the item
     */
    public static void setItemMeta(ItemStack i, ItemMeta meta){
        meta = meta.clone();
        meta.getPersistentDataContainer().remove(TYPE_KEY);
        i.setItemMeta(meta);
    }

    public static Material getStoredType(ItemMeta meta){
        return stringToMaterial(meta.getPersistentDataContainer().get(TYPE_KEY, PersistentDataType.STRING), null);
    }

    public static void updateStoredType(ItemMeta meta, Material newType){
        if (meta.getPersistentDataContainer().has(TYPE_KEY, PersistentDataType.STRING))
            meta.getPersistentDataContainer().set(TYPE_KEY, PersistentDataType.STRING, newType.toString());
    }

    public static Material stringToMaterial(String material, Material def){
        if (material == null || material.isEmpty()) return def;
        return Catch.catchOrElse(() -> Material.valueOf(material), def);
    }

    public static String serialize(ItemStack itemStack) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeObject(itemStack);

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception ignored) {}
        return null;
    }

    public static ItemStack deserialize(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            ItemStack i = (ItemStack) dataInput.readObject();
            dataInput.close();
            return i;
        } catch (ClassNotFoundException | IOException ignored) {}
        return null;
    }

    /**
     * An item's text may look the same, but be stored differently on the item's meta due to
     * text component differences. This method reads and writes this text so it is
     * stored on the item meta in a consistent fashion. <br>
     * This method both modifies and returns the same item meta, it does not create a copy.
     */
    public static ItemMeta normalizeItemText(ItemMeta meta){
        if (meta == null) return null;
        if (meta.hasLore() && meta.getLore() != null){
            List<String> newLore = new ArrayList<>();
            for (String s : meta.getLore()){
                newLore.add(Utils.chat(s));
            }

            meta.setLore(newLore);
        }
        if (meta.hasDisplayName()) meta.setDisplayName(meta.getDisplayName());

        return meta;
    }

    public static double getTotalAttributeValue(Collection<AttributeModifier> modifiers, double baseValue, double baseScalar, double baseMultiplier, Predicate<AttributeModifier> filter){
        double value = baseValue;
        if (modifiers != null) {
            double number = modifiers.stream().filter(m -> m.getOperation() == AttributeModifier.Operation.ADD_NUMBER && filter.test(m)).mapToDouble(AttributeModifier::getAmount).sum();
            double scalar = 1 + baseScalar + modifiers.stream().filter(m -> m.getOperation() == AttributeModifier.Operation.ADD_SCALAR && !filter.test(m)).mapToDouble(AttributeModifier::getAmount).sum();
            double multiplier = 1 * baseMultiplier;
            for (AttributeModifier m : modifiers){
                if (m.getOperation() == AttributeModifier.Operation.MULTIPLY_SCALAR_1 && !filter.test(m))
                    multiplier *= 1 + m.getAmount();
            }
            value = (multiplier * ((value + number) * scalar));
        }
        return value;
    }

    public static Collection<AttributeModifier> getAllWornModifiers(Player p, Attribute attribute, ItemStack except, boolean includeHands, Boolean hand){
        Collection<AttributeModifier> modifiers = new HashSet<>();
        // iterate through each item except main hand, since the attributes of the main hand
        // should not affect its own attributes
        Map<EquipmentSlot, ItemBuilder> items = EntityCache.getAndCacheProperties(p).getIterable(includeHands, hand);
        for (EquipmentSlot slot : items.keySet()){
            ItemBuilder item = items.get(slot);
            if (item.getItem().equals(except)) continue;
            Collection<AttributeModifier> itemModifiers = item.getMeta().getAttributeModifiers(attribute);
            if (itemModifiers == null) continue;
            modifiers.addAll(itemModifiers.stream().filter(m -> m.getSlot() == slot).collect(Collectors.toSet()));
        }
        return modifiers;
    }
}

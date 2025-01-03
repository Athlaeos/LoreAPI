package me.athlaeos.lapi.utils;

import me.athlaeos.lapi.format.DisplayFormat;
import me.athlaeos.lapi.format.DisplayFormatRegistry;
import me.athlaeos.lapi.localization.Translator;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {
    private ItemStack item;
    private ItemMeta meta;

    public ItemBuilder copy(){
        return new ItemBuilder(item.clone(), meta.clone());
    }

    public ItemBuilder(Material m){
        this.item = new ItemStack(m);
        this.meta = ItemUtils.getItemMeta(item);
    }

    public ItemBuilder(ItemStack i, ItemMeta m){
        this.item = i.clone();
        this.meta = m.clone();
    }

    public ItemBuilder(ItemStack i){
        this.item = i.clone();
        this.meta = ItemUtils.getItemMeta(item);
    }

    public ItemBuilder type(Material type){
        item.setType(type);
        ItemUtils.setItemMeta(item, meta);
        meta = ItemUtils.getItemMeta(item);
        return this;
    }

    public ItemBuilder translate(){
        Translator.translateItemMeta(meta);
        return this;
    }

    public ItemBuilder data(int data){
        if (data > 0) meta.setCustomModelData(data);
        else meta.setCustomModelData(null);
        return this;
    }

    public ItemBuilder intTag(NamespacedKey key, int tag){
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, tag);
        return this;
    }

    public ItemBuilder floatTag(NamespacedKey key, float tag){
        meta.getPersistentDataContainer().set(key, PersistentDataType.FLOAT, tag);
        return this;
    }

    public ItemBuilder doubleTag(NamespacedKey key, double tag){
        meta.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, tag);
        return this;
    }

    public ItemBuilder stringTag(NamespacedKey key, String tag){
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, tag);
        return this;
    }

    public ItemBuilder name(String name){
        meta.setDisplayName(Utils.chat(name));
        return this;
    }

    public ItemBuilder lore(List<String> lore){
        if (lore == null) {
            meta.setLore(new ArrayList<>());
            return this;
        }
        meta.setLore(Utils.chat(lore));
        return this;
    }

    public ItemBuilder lore(String... lore){
        if (lore == null) return this;
        return lore(Arrays.asList(lore));
    }

    public ItemBuilder appendLore(List<String> lore){
        if (lore == null) return this;
        List<String> l = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
        l.addAll(Utils.chat(lore));
        meta.setLore(l);
        return this;
    }

    public ItemBuilder prependLore(List<String> lore){
        if (lore == null) return this;
        List<String> l = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
        lore = new ArrayList<>(Utils.chat(lore));
        lore.addAll(l);
        meta.setLore(lore);
        return this;
    }

    public ItemBuilder placeholderLore(String placeholder, List<String> replaceWith){
        return lore(StringUtils.setListPlaceholder(meta.hasLore() && meta.getLore() != null ? meta.getLore() : new ArrayList<>(), placeholder, replaceWith));
    }

    public ItemBuilder appendLore(String... lore){
        if (lore == null) return this;
        return appendLore(Arrays.asList(lore));
    }

    public ItemBuilder prependLore(String... lore){
        if (lore == null) return this;
        return prependLore(new ArrayList<>(List.of(lore)));
    }

    public ItemBuilder amount(int amount){
        item.setAmount(Math.min(amount, 64));
        return this;
    }

    public ItemBuilder flag(ItemFlag... flags){
        meta.addItemFlags(flags);
        return this;
    }

    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder color(Color color){
        if (meta instanceof LeatherArmorMeta m){
            m.setColor(color);
        } else if (meta instanceof PotionMeta m){
            m.setColor(color);
        }
        if (MinecraftVersion.currentVersionNewerThan(MinecraftVersion.MINECRAFT_1_21)){
            if (meta instanceof ColorableArmorMeta c){
                c.setColor(color);
            }
        }
        return this;
    }

    public ItemBuilder disEnchant(){
        if (meta instanceof EnchantmentStorageMeta m) m.getStoredEnchants().keySet().forEach(m::removeStoredEnchant);
        else item.getEnchantments().keySet().forEach(item::removeEnchantment);
        return this;
    }

    public ItemBuilder disEnchant(Enchantment e){
        if (meta instanceof EnchantmentStorageMeta m) m.removeStoredEnchant(e);
        else meta.removeEnchant(e);
        return this;
    }

    public ItemBuilder enchant(Enchantment e, int level){
        if (meta instanceof EnchantmentStorageMeta m) m.addStoredEnchant(e, level, true);
        else meta.addEnchant(e, level, true);
        return this;
    }

    public ItemStack get(){
        if (ItemUtils.isEmpty(this.item)) return null;
        ItemUtils.setItemMeta(this.item, meta);
        return item;
    }

    public ItemBuilder wipeAttributes(){
        meta.setAttributeModifiers(null);
        return this;
    }

    public ItemBuilder displayFormat(DisplayFormat format){
        DisplayFormatRegistry.setFormat(meta, format);
        return this;
    }

    public ItemStack getItem() { return item; }
    public ItemMeta getMeta() { return meta; }
    public ItemBuilder setItem(ItemStack item) { this.item = item; return this; }
    public ItemBuilder setMeta(ItemMeta meta) { this.meta = meta; return this; }
}

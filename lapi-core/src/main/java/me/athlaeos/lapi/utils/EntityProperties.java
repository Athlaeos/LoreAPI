package me.athlaeos.lapi.utils;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityProperties {
    private ItemBuilder helmet = null;
    private ItemBuilder chestplate = null;
    private ItemBuilder leggings = null;
    private ItemBuilder boots = null;
    private ItemBuilder mainHand = null;
    private ItemBuilder offHand = null;

    public EntityProperties(){}

    public EntityProperties(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack mainHand, ItemStack offHand){
        this.helmet = ItemUtils.isEmpty(helmet) ? null : new ItemBuilder(helmet);
        this.chestplate = ItemUtils.isEmpty(chestplate) ? null : new ItemBuilder(chestplate);
        this.leggings = ItemUtils.isEmpty(leggings) ? null : new ItemBuilder(leggings);
        this.boots = ItemUtils.isEmpty(boots) ? null : new ItemBuilder(boots);
        this.mainHand = ItemUtils.isEmpty(mainHand) ? null : new ItemBuilder(mainHand);
        this.offHand = ItemUtils.isEmpty(offHand) ? null : new ItemBuilder(offHand);
    }

    public ItemBuilder getHelmet() { return helmet; }
    public void setHelmet(ItemStack helmet) { this.helmet = ItemUtils.isEmpty(helmet) ? null : new ItemBuilder(helmet); }
    public ItemBuilder getChestplate() { return chestplate; }
    public void setChestplate(ItemStack chestplate) { this.chestplate = ItemUtils.isEmpty(chestplate) ? null : new ItemBuilder(chestplate); }
    public ItemBuilder getBoots() { return boots; }
    public void setBoots(ItemStack boots) { this.boots = ItemUtils.isEmpty(boots) ? null : new ItemBuilder(boots); }
    public ItemBuilder getLeggings() { return leggings; }
    public void setLeggings(ItemStack leggings) { this.leggings = ItemUtils.isEmpty(leggings) ? null : new ItemBuilder(leggings); }
    public ItemBuilder getMainHand() { return mainHand; }
    public void setMainHand(ItemStack mainHand) { this.mainHand = ItemUtils.isEmpty(mainHand) ? null : new ItemBuilder(mainHand); }
    public ItemBuilder getOffHand() { return offHand; }
    public void setOffHand(ItemStack offHand) { this.offHand = ItemUtils.isEmpty(offHand) ? null : new ItemBuilder(offHand); }

    /**
     * Returns a list containing all the entity's equipment.
     * @param includeHands whether the hand items should be included also
     * @param hand true if only main hand should be returned, false if only offhand, null if both
     * @return a map containing all the entity's equipment, mapped to their respective equipment slots
     */
    public Map<EquipmentSlot, ItemBuilder> getIterable(boolean includeHands, Boolean hand){
        Map<EquipmentSlot, ItemBuilder> iterable = new HashMap<>();
        if (helmet != null) iterable.put(EquipmentSlot.HEAD, helmet);
        if (chestplate != null) iterable.put(EquipmentSlot.CHEST, chestplate);
        if (leggings != null) iterable.put(EquipmentSlot.LEGS, leggings);
        if (boots != null) iterable.put(EquipmentSlot.FEET, boots);
        if (includeHands){
            if ((hand == null || hand) && mainHand != null) iterable.put(EquipmentSlot.HAND, mainHand);
            if ((hand == null || !hand) && offHand != null) iterable.put(EquipmentSlot.OFF_HAND, offHand);
        }
        return iterable;
    }

    public List<ItemBuilder> getHands(){
        List<ItemBuilder> iterable = new ArrayList<>();
        if (mainHand != null) iterable.add(mainHand);
        if (offHand != null) iterable.add(offHand);
        return iterable;
    }
}
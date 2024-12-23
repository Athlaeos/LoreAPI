package me.athlaeos.lapi.utils;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;

import java.util.*;

public class DefaultItemProperties {
    private static final Map<Material, Map<Attribute, AttributeModifier>> vanillaAttributes = new HashMap<>();

    static {
        addDefaultModifiers(Material.WOODEN_SWORD, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(4), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1.6));
        addDefaultModifiers(Material.WOODEN_PICKAXE, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(2), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1.2));
        addDefaultModifiers(Material.WOODEN_SHOVEL, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(2.5), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1));
        addDefaultModifiers(Material.WOODEN_AXE, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(7), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(0.8));
        addDefaultModifiers(Material.WOODEN_HOE, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(1), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1));

        addDefaultModifiers(Material.STONE_SWORD, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(5), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1.6));
        addDefaultModifiers(Material.STONE_PICKAXE, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(3), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1.2));
        addDefaultModifiers(Material.STONE_SHOVEL, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(3.5), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1));
        addDefaultModifiers(Material.STONE_AXE, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(9), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(0.8));
        addDefaultModifiers(Material.STONE_HOE, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(1), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(2));

        addDefaultModifiers(Material.GOLDEN_SWORD, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(4), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1.6));
        addDefaultModifiers(Material.GOLDEN_PICKAXE, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(2), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1.2));
        addDefaultModifiers(Material.GOLDEN_SHOVEL, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(2.5), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1));
        addDefaultModifiers(Material.GOLDEN_AXE, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(7), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(0.8));
        addDefaultModifiers(Material.GOLDEN_HOE, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(1), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1));

        addDefaultModifiers(Material.IRON_SWORD, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(6), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1.6));
        addDefaultModifiers(Material.IRON_PICKAXE, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(4), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1.2));
        addDefaultModifiers(Material.IRON_SHOVEL, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(4.5), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1));
        addDefaultModifiers(Material.IRON_AXE, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(9), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(0.9));
        addDefaultModifiers(Material.IRON_HOE, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(1), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(3));

        addDefaultModifiers(Material.DIAMOND_SWORD, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(7), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1.6));
        addDefaultModifiers(Material.DIAMOND_PICKAXE, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(5), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1.2));
        addDefaultModifiers(Material.DIAMOND_SHOVEL, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(5.5), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1));
        addDefaultModifiers(Material.DIAMOND_AXE, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(9), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1));
        addDefaultModifiers(Material.DIAMOND_HOE, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(1), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(4));

        addDefaultModifiers(Material.NETHERITE_SWORD, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(8), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1.6));
        addDefaultModifiers(Material.NETHERITE_PICKAXE, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(6), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1.2));
        addDefaultModifiers(Material.NETHERITE_SHOVEL, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(6.5), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1));
        addDefaultModifiers(Material.NETHERITE_AXE, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(10), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1));
        addDefaultModifiers(Material.NETHERITE_HOE, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(1), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(4));

        addDefaultModifiers(Material.LEATHER_HELMET, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(1));
        addDefaultModifiers(Material.LEATHER_CHESTPLATE, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(3));
        addDefaultModifiers(Material.LEATHER_LEGGINGS, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(2));
        addDefaultModifiers(Material.LEATHER_BOOTS, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(1));

        addDefaultModifiers(Material.CHAINMAIL_HELMET, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(2));
        addDefaultModifiers(Material.CHAINMAIL_CHESTPLATE, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(5));
        addDefaultModifiers(Material.CHAINMAIL_LEGGINGS, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(4));
        addDefaultModifiers(Material.CHAINMAIL_BOOTS, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(1));

        addDefaultModifiers(Material.GOLDEN_HELMET, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(2));
        addDefaultModifiers(Material.GOLDEN_CHESTPLATE, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(5));
        addDefaultModifiers(Material.GOLDEN_LEGGINGS, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(3));
        addDefaultModifiers(Material.GOLDEN_BOOTS, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(1));

        addDefaultModifiers(Material.IRON_HELMET, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(2));
        addDefaultModifiers(Material.IRON_CHESTPLATE, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(6));
        addDefaultModifiers(Material.IRON_LEGGINGS, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(5));
        addDefaultModifiers(Material.IRON_BOOTS, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(2));

        addDefaultModifiers(Material.DIAMOND_HELMET, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(3), new AttributeModifierBuilder(Attribute.GENERIC_ARMOR_TOUGHNESS, "default_armor_toughness").setAmount(2));
        addDefaultModifiers(Material.DIAMOND_CHESTPLATE, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(8), new AttributeModifierBuilder(Attribute.GENERIC_ARMOR_TOUGHNESS, "default_armor_toughness").setAmount(2));
        addDefaultModifiers(Material.DIAMOND_LEGGINGS, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(6), new AttributeModifierBuilder(Attribute.GENERIC_ARMOR_TOUGHNESS, "default_armor_toughness").setAmount(2));
        addDefaultModifiers(Material.DIAMOND_BOOTS, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(3), new AttributeModifierBuilder(Attribute.GENERIC_ARMOR_TOUGHNESS, "default_armor_toughness").setAmount(2));

        addDefaultModifiers(Material.NETHERITE_HELMET, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(3), new AttributeModifierBuilder(Attribute.GENERIC_ARMOR_TOUGHNESS, "default_armor_toughness").setAmount(3), new AttributeModifierBuilder(Attribute.GENERIC_KNOCKBACK_RESISTANCE, "default_knockback_resistance").setAmount(0.1));
        addDefaultModifiers(Material.NETHERITE_CHESTPLATE, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(8), new AttributeModifierBuilder(Attribute.GENERIC_ARMOR_TOUGHNESS, "default_armor_toughness").setAmount(3), new AttributeModifierBuilder(Attribute.GENERIC_KNOCKBACK_RESISTANCE, "default_knockback_resistance").setAmount(0.1));
        addDefaultModifiers(Material.NETHERITE_LEGGINGS, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(6), new AttributeModifierBuilder(Attribute.GENERIC_ARMOR_TOUGHNESS, "default_armor_toughness").setAmount(3), new AttributeModifierBuilder(Attribute.GENERIC_KNOCKBACK_RESISTANCE, "default_knockback_resistance").setAmount(0.1));
        addDefaultModifiers(Material.NETHERITE_BOOTS, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(3), new AttributeModifierBuilder(Attribute.GENERIC_ARMOR_TOUGHNESS, "default_armor_toughness").setAmount(3), new AttributeModifierBuilder(Attribute.GENERIC_KNOCKBACK_RESISTANCE, "default_knockback_resistance").setAmount(0.1));

        addDefaultModifiers(Material.TURTLE_HELMET, new AttributeModifierBuilder(Attribute.GENERIC_ARMOR, "default_armor").setAmount(2));

        addDefaultModifiers(Material.TRIDENT, new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_DAMAGE, "default_attack_damage").setAmount(9), new AttributeModifierBuilder(Attribute.GENERIC_ATTACK_SPEED, "default_attack_speed").setAmount(1.1));
    }

    private static void addDefaultModifiers(Material material, AttributeModifierBuilder... builders){
        for (AttributeModifierBuilder builder : builders){
            Map<Attribute, AttributeModifier> modifiers = vanillaAttributes.getOrDefault(material, new HashMap<>());
            modifiers.put(builder.attribute, builder.build());
            vanillaAttributes.put(material, modifiers);
        }
    }

    /**
     * Vanilla items only have 1 default stat of each, so only 1 modifier needs to be returned.<br>
     * GENERIC_ATTACK_DAMAGE is always 1 lower than what shows on a vanilla item, due to player base damage
     * being included in the stat number on the item.
     * @param attribute The attribute to get the base stat from the material type
     * @param item The material type
     * @return The default AttributeModifier of the given Attribute, or null if none is present.
     */
    public static AttributeModifier getDefaultModifier(Attribute attribute, Material item){
        return vanillaAttributes.getOrDefault(item, new HashMap<>()).get(attribute);
    }
    
    private static class AttributeModifierBuilder{
        private final Attribute attribute;
        private double amount = 0;
        private EquipmentSlot slot = EquipmentSlot.HAND;
        private final String name;
        private AttributeModifier.Operation operation = AttributeModifier.Operation.ADD_NUMBER;
        private UUID uuid = UUID.randomUUID();
        public AttributeModifierBuilder(Attribute attribute, String name){
            this.attribute = attribute;
            this.name = name;
        }

        public Attribute getAttribute() { return attribute; }
        public AttributeModifierBuilder setAmount(double amount) {
            if (attribute == Attribute.GENERIC_ATTACK_DAMAGE) amount--; // Attack damage is 1 lower than it really is due to player base attack damage
            else if (attribute == Attribute.GENERIC_ATTACK_SPEED) amount -= 4; // Attack speed is 4 lower than it really is due to player base attack speed
            this.amount = amount;
            return this;
        }

        public AttributeModifierBuilder setSlot(EquipmentSlot slot) {
            this.slot = slot;
            return this;
        }

        public AttributeModifierBuilder setOperation(AttributeModifier.Operation operation) {
            this.operation = operation;
            return this;
        }

        public AttributeModifierBuilder setUuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public AttributeModifier build(){
            return new AttributeModifier(uuid, name, amount, operation, slot);
        }
    }
}

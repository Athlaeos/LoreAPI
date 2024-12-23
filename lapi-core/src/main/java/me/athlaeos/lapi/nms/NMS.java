package me.athlaeos.lapi.nms;

import io.netty.channel.Channel;
import me.athlaeos.lapi.utils.ItemBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface NMS {
    default void onEnable(){

    }

    Collection<String> equipmentMatches = Set.of("HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS", "ELYTRA", "CARVED_PUMPKIN", "SKULL", "HEAD");
    default boolean isArmor(ItemBuilder item){
        return equipmentMatches.stream().anyMatch(e -> item.getItem().getType().toString().contains(e));
    }

    Channel channel(Player p);

    void readItemMetaPacket(Player p, Object packet);

    void sendPlayerItem(Player p, ItemStack item, int slot);

    void sendContainerItem(Player p, ItemStack item, int slot, int container, int stateId);

    void sendContainerContents(Player p, List<ItemStack> items, int container, int stateId);

    int getOpenContainerID(Player p);

    int getOpenContainerState(Player p);

    default int getMaxDurability(ItemBuilder item){
        return item.getItem().getType().getMaxDurability();
    }

    default void setToolTipStyle(ItemBuilder item, String style){
        // do nothing
    }

    default void setModel(ItemBuilder item, String model){
        // do nothing
    }

    default void setEnchantmentGlint(ItemBuilder item, boolean glint){
        // do nothing
    }

    default int getDurability(ItemBuilder item){
        int max = getMaxDurability(item);
        if (max <= 0 || !(item.getMeta() instanceof Damageable d)) return 0;
        return max - d.getDamage();
    }

    default Enchantment getEnchantmentFromString(String enchantmentKey){
        NamespacedKey key = NamespacedKey.fromString(enchantmentKey);
        if (key == null) return null;
        return Enchantment.getByKey(key);
    }
}

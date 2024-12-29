package me.athlaeos.nms;

import io.netty.channel.Channel;
import me.athlaeos.lapi.nms.NMS;
import me.athlaeos.lapi.utils.ItemBuilder;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.List;

public class NMS_v1_20_R3 implements NMS {

    @Override
    @SuppressWarnings("all")
    public Channel channel(Player p) {
        try {
            Field c = ServerGamePacketListenerImpl.class.getSuperclass().getDeclaredField("c");
            c.setAccessible(true);
            return ((Connection) c.get(((CraftPlayer) p).getHandle().connection)).channel;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readItemMetaPacket(Player p, Object packet) {

    }

    @Override
    public void sendPlayerItem(Player p, ItemStack item, int slot) {

    }

    @Override
    public void sendContainerItem(Player p, ItemStack item, int slot, int container, int stateId) {

    }

    @Override
    public void sendContainerContents(Player p, List<ItemStack> items, int container, int stateId) {

    }

    @Override
    public int getOpenContainerID(Player p) {
        ServerPlayer player = ((CraftPlayer) p).getHandle();
        AbstractContainerMenu menu = player.containerMenu;
        if (menu == null) return -1;
        return menu.containerId;
    }

    @Override
    public int getOpenContainerState(Player p) {
        ServerPlayer player = ((CraftPlayer) p).getHandle();
        AbstractContainerMenu menu = player.containerMenu;
        if (menu == null) return -1;
        return menu.getStateId();
    }

    @Override
    public Enchantment getEnchantmentFromString(String enchantmentKey) {
        NamespacedKey key = NamespacedKey.fromString(enchantmentKey);
        if (key == null) return null;
        return Registry.ENCHANTMENT.get(key);
    }
}
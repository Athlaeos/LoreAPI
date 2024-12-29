package me.athlaeos.nms;

import io.netty.channel.Channel;
import me.athlaeos.lapi.LoreAPIPlugin;
import me.athlaeos.lapi.placeholder.PlaceholderRegistry;
import me.athlaeos.lapi.network.PacketListener;
import me.athlaeos.lapi.nms.NMS;
import me.athlaeos.lapi.utils.ItemBuilder;
import me.athlaeos.lapi.utils.ItemUtils;
import me.athlaeos.lapi.utils.Utils;
import net.minecraft.core.NonNullList;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.ArrayList;
import java.util.List;

public class NMS_v1_21_R3 implements NMS {
    private final Field packetSlotItem;
    private final Field packetContainerItems;
    private final Field packetPlayerItem;

    @SuppressWarnings("all")
    public NMS_v1_21_R3(){
        Field slotField = null;
        Field playerField = null;
        Field containerField = null;
        try {
            Class<ClientboundContainerSetSlotPacket> containerSlotClass = ClientboundContainerSetSlotPacket.class;
            slotField = containerSlotClass.getDeclaredField("e");
            slotField.setAccessible(true);

            Class<ClientboundSetPlayerInventoryPacket> playerInventoryClass = ClientboundSetPlayerInventoryPacket.class;
            playerField = playerInventoryClass.getDeclaredField("c");
            playerField.setAccessible(true);

            Class<ClientboundContainerSetContentPacket> containerInventoryClass = ClientboundContainerSetContentPacket.class;
            containerField = containerInventoryClass.getDeclaredField("d");
            containerField.setAccessible(true);
        } catch (NoSuchFieldException | InaccessibleObjectException exception){
            LoreAPIPlugin.logSevere("Could not initialize packet listener: ");
            Utils.logStackTrace(exception, true);
        }
        packetSlotItem = slotField;
        packetPlayerItem = playerField;
        packetContainerItems = containerField;
    }

    @Override
    public boolean isArmor(ItemBuilder item) {
        return NMS.super.isArmor(item) || item.getMeta().hasEquippable();
    }

    @Override
    @SuppressWarnings("all")
    public Channel channel(Player p) {
        try {
            Field c = ServerGamePacketListenerImpl.class.getSuperclass().getDeclaredField("e");
            c.setAccessible(true);
            return ((Connection) c.get(((CraftPlayer) p).getHandle().connection)).channel;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readItemMetaPacket(Player p, Object packet) {
        if (p.getGameMode() == GameMode.CREATIVE) return;
        switch (packet){
            case ClientboundContainerSetSlotPacket itemPacket -> {
                ItemStack oldItem = itemPacket.getItem();
                if (oldItem == null) return;
                org.bukkit.inventory.ItemStack bukkitStack = CraftItemStack.asBukkitCopy(oldItem);
                bukkitStack = PlaceholderRegistry.parseItemStack(bukkitStack, p);
                if (ItemUtils.isEmpty(bukkitStack)) return;
                try {
                    packetSlotItem.set(itemPacket, CraftItemStack.asNMSCopy(bukkitStack));
                } catch (IllegalAccessException ignored){
                    LoreAPIPlugin.logWarning("Could not write new ItemStack meta to container slot packet");
                }
            }
            case ClientboundSetPlayerInventoryPacket itemPacket -> {
                ItemStack oldItem = itemPacket.contents();
                if (oldItem == null) return;
                org.bukkit.inventory.ItemStack bukkitStack = CraftItemStack.asBukkitCopy(oldItem);
                bukkitStack = PlaceholderRegistry.parseItemStack(bukkitStack, p);
                if (ItemUtils.isEmpty(bukkitStack)) return;
                try {
                    packetPlayerItem.set(itemPacket, CraftItemStack.asNMSCopy(bukkitStack));
                } catch (IllegalAccessException ignored){
                    LoreAPIPlugin.logWarning("Could not write new ItemStack meta to player inventory packet");
                }
            }
            case ClientboundContainerSetContentPacket itemPacket -> {
                List<ItemStack> oldItems = itemPacket.getItems();
                List<ItemStack> newItems = new ArrayList<>();
                for (ItemStack item : oldItems){
                    if (item == null) continue;
                    org.bukkit.inventory.ItemStack bukkitStack = PlaceholderRegistry.parseItemStack(CraftItemStack.asBukkitCopy(item), p);
                    if (ItemUtils.isEmpty(bukkitStack)) bukkitStack = new org.bukkit.inventory.ItemStack(Material.AIR);
                    newItems.add(CraftItemStack.asNMSCopy(bukkitStack));
                }
                try {
                    packetContainerItems.set(itemPacket, newItems);
                } catch (IllegalAccessException ignored){
                    LoreAPIPlugin.logWarning("Could not write new ItemStacks to container contents packet");
                }
            }
            default -> {}
        }
    }

    @Override
    public void sendPlayerItem(Player p, org.bukkit.inventory.ItemStack item, int slot) {
        PacketListener.sendPacket(p, new ClientboundSetPlayerInventoryPacket(slot, CraftItemStack.asNMSCopy(item)));
    }

    @Override
    public void sendContainerItem(Player p, org.bukkit.inventory.ItemStack item, int slot, int container, int stateId) {
        PacketListener.sendPacket(p, new ClientboundContainerSetSlotPacket(container, stateId, slot, CraftItemStack.asNMSCopy(item)));
    }

    @Override
    public void sendContainerContents(Player p, List<org.bukkit.inventory.ItemStack> items, int container, int stateId) {
        NonNullList<ItemStack> newItems = NonNullList.create();
        newItems.addAll(items.stream().map(CraftItemStack::asNMSCopy).toList());
        PacketListener.sendPacket(p, new ClientboundContainerSetContentPacket(container, stateId, newItems, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))));
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

    @Override
    public void setToolTipStyle(ItemBuilder item, String style) {
        item.getMeta().setTooltipStyle(NamespacedKey.fromString(style));
    }

    @Override
    public void setModel(ItemBuilder item, String model) {
        item.getMeta().setItemModel(NamespacedKey.fromString(model));
    }

    @Override
    public void setEnchantmentGlint(ItemBuilder item, boolean glint) {
        item.getMeta().setEnchantmentGlintOverride(glint);
    }
}
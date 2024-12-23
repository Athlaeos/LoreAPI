package me.athlaeos.lapi.listeners;

import me.athlaeos.lapi.LoreAPIPlugin;
import me.athlaeos.lapi.utils.EntityCache;
import me.athlaeos.lapi.utils.ItemBuilder;
import me.athlaeos.lapi.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArmorSwitchListener implements Listener {
    private static final Map<UUID, DelayedEquipmentUpdate> taskLimiters = new HashMap<>();

    private void updateArmor(Player who){
        DelayedEquipmentUpdate update = taskLimiters.get(who.getUniqueId());
        if (update != null) update.refresh();
        else {
            update = new DelayedEquipmentUpdate(who);
            update.runTaskTimer(LoreAPIPlugin.getInstance(), 1L, 1L);
        }
        taskLimiters.put(who.getUniqueId(), update);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        EntityCache.getAndCacheProperties(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryEquip(InventoryClickEvent e){
        if (e.isCancelled() || e.getClickedInventory() == null) return;
        if (e.getClickedInventory().getType() == InventoryType.PLAYER || e.getClickedInventory().getType() == InventoryType.CREATIVE){
            if (e.getSlotType() == InventoryType.SlotType.ARMOR){
                if (ItemUtils.isEmpty(e.getCurrentItem()) && ItemUtils.isEmpty(e.getCursor())) return; // both clicked slot and cursor are empty, no need to do anything
                // cursor or clicked item are empty, but not both. items are switched and therefore armor may be updated
                updateArmor((Player) e.getWhoClicked());
            } else if (e.getClick().isShiftClick() && !ItemUtils.isEmpty(e.getCurrentItem())){
                ItemBuilder clicked = new ItemBuilder(e.getCurrentItem());
                // Shift clicking item could mean it is shift clicked into an armor slot, so trigger an update
                if (LoreAPIPlugin.getNms().isArmor(clicked)) updateArmor((Player) e.getWhoClicked());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDispenserEquip(BlockDispenseArmorEvent e){
        if (e.isCancelled()) return;
        if (e.getTargetEntity() instanceof Player p){
            // armor equipped through dispenser, update equipment
            updateArmor(p);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHandEquip(PlayerInteractEvent e){
        if (e.useItemInHand() == Event.Result.DENY || ItemUtils.isEmpty(e.getItem()) || !e.getItem().getType().isItem()) return;
        ItemBuilder clicked = new ItemBuilder(e.getItem());
        // armor was clicked and might be swapped out, update equipment
        if (LoreAPIPlugin.getNms().isArmor(clicked)) updateArmor(e.getPlayer());
    }

    private static class DelayedEquipmentUpdate extends BukkitRunnable{
        private static final int delay = 30; // after 1.5 seconds update equipment
        private int timer = delay;
        private final Player who;

        public DelayedEquipmentUpdate(Player who){
            this.who = who;
        }

        @Override
        public void run() {
            if (timer <= 0){
                EntityCache.resetEquipment(who);
                taskLimiters.remove(who.getUniqueId());
                cancel();
            } else {
                timer--;
            }
        }

        public void refresh(){
            timer = delay;
        }
    }
}

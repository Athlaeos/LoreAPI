package me.athlaeos.lapi.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;

public class StatChangeListener implements Listener {

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPotionEffectChange(EntityPotionEffectEvent e){
        if (e.isCancelled() || !(e.getEntity() instanceof Player p)) return;
        p.updateInventory();
    }
}

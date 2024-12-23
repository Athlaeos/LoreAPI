package me.athlaeos.lapi.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class GamemodeListener implements Listener {

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGamemodeChange(PlayerGameModeChangeEvent e){
        if (e.isCancelled()) return;
        e.getPlayer().updateInventory();
    }
}

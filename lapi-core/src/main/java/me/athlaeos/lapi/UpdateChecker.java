package me.athlaeos.lapi;

import me.athlaeos.lapi.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Consumer;

public class UpdateChecker {
    private static final int resourceId = 10000; // TODO add spigot plugin ID

    private static void getVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(LoreAPIPlugin.getInstance(), () -> {
            try (InputStream is = URI.create("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId + "/~").toURL().openStream(); Scanner scann = new Scanner(is)) {
                if (scann.hasNext()) {
                    consumer.accept(scann.next());
                }
            } catch (IOException e) {
                LoreAPIPlugin.getInstance().getLogger().info("Unable to check for updates: " + e.getMessage());
            }
        });
    }

    public static void checkUpdate(){
        getVersion(v -> {
            if (!LoreAPIPlugin.getInstance().getDescription().getVersion().equals(v)) {
                Bukkit.getScheduler().runTask(LoreAPIPlugin.getInstance(), () -> {
                    LoreAPIPlugin.logInfo("There is a new version of LoreAPI available: " + v);
                    LoreAPIPlugin.logInfo("Please don't forget to look at the change notes, " +
                            "this update might be important and additional steps may be required to have the update go smoothly");
                    LoreAPIPlugin.logInfo(" "); // TODO insert download link here
                });
                Bukkit.getPluginManager().registerEvents(new Listener() {
                    private final Collection<UUID> messagedOperators = new HashSet<>();

                    @EventHandler
                    public void onOperatorJoin(PlayerJoinEvent e){
                        if (!e.getPlayer().isOp() || messagedOperators.contains(e.getPlayer().getUniqueId())) return;
                        messagedOperators.add(e.getPlayer().getUniqueId());
                        e.getPlayer().sendMessage(
                                Utils.chat("&dA new version of LoreAPI is available"),
                                Utils.chat("&dI recommend you take a look at the update notes"),
                                Utils.chat("&dto see the importance of this update."),
                                Utils.chat("&6")); // TODO insert download link here
                    }
                }, LoreAPIPlugin.getInstance());
            }
        });
    }
}

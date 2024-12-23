package me.athlaeos.lapi.utils;

import me.athlaeos.lapi.LoreAPIPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class Reloader {
    private static final Map<Class<? extends Reloadable>, Reloadable> reloadables = new HashMap<>();

    public static void register(Reloadable reloadable){
        reloadables.put(reloadable.getClass(), reloadable);
    }

    public static void reload(){
        Collection<Reloadable> async = reloadables.values().stream().filter(Reloadable::canReloadAsync).collect(Collectors.toSet());
        Collection<Reloadable> sync = reloadables.values().stream().filter(r -> !r.canReloadAsync()).collect(Collectors.toSet());

        LoreAPIPlugin.getInstance().getServer().getScheduler().runTask(LoreAPIPlugin.getInstance(), () -> sync.forEach(Reloadable::reload));
        LoreAPIPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(LoreAPIPlugin.getInstance(), () -> async.forEach(Reloadable::reload));
    }
}

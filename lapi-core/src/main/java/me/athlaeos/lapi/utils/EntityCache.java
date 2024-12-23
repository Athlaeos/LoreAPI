package me.athlaeos.lapi.utils;

import me.athlaeos.lapi.LoreAPIPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class EntityCache {
    private static final long CACHE_REFRESH_DELAY = 10000;
    private static final long CACHE_CLEANUP_DELAY = 600000;
    private static final Map<UUID, EntityProperties> cachedProperties = new HashMap<>();
    private static final Map<UUID, Long> lastCacheRefreshMap = new HashMap<>();
    private static long lastCacheCleanup = System.currentTimeMillis();

    public static EntityProperties getAndCacheProperties(LivingEntity entity){
        attemptCacheCleanup();
        if (lastCacheRefreshMap.getOrDefault(entity.getUniqueId(), 0L) + CACHE_REFRESH_DELAY <= System.currentTimeMillis()){
            // delay expired, cache properties
            cachedProperties.put(entity.getUniqueId(), getEntityProperties(entity, false));
        }
        return cachedProperties.get(entity.getUniqueId());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void resetHands(LivingEntity entity){
        cachedProperties.put(entity.getUniqueId(), updateProperties(cachedProperties.getOrDefault(entity.getUniqueId(), getAndCacheProperties(entity)),
                entity, true));
        if (entity instanceof Player p) p.updateInventory();
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void resetEquipment(LivingEntity entity){
        cachedProperties.put(entity.getUniqueId(), updateProperties(cachedProperties.getOrDefault(entity.getUniqueId(), getAndCacheProperties(entity)),
                entity, false));
        if (entity instanceof Player p) p.updateInventory();
    }

    public static void removeProperties(LivingEntity entity){
        cachedProperties.remove(entity.getUniqueId());
    }

    public static void attemptCacheCleanup(){
        LoreAPIPlugin.getInstance().getServer().getScheduler().runTask(LoreAPIPlugin.getInstance(), () -> {
            if (lastCacheCleanup + CACHE_CLEANUP_DELAY < System.currentTimeMillis()){
                Collection<UUID> uuids = new HashSet<>(cachedProperties.keySet());
                uuids.forEach(u -> {
                    Entity entity = LoreAPIPlugin.getInstance().getServer().getEntity(u);
                    if (entity == null || !entity.isValid()){
                        cachedProperties.remove(u);
                        lastCacheRefreshMap.remove(u);
                    }
                });
                lastCacheCleanup = System.currentTimeMillis();
            }
        });
    }

    public static EntityProperties getEntityProperties(LivingEntity e, boolean handsOnly){
        EntityProperties equipment = new EntityProperties();
        if (e == null) return equipment;
        return updateProperties(equipment, e, handsOnly);
    }

    public static EntityProperties updateProperties(EntityProperties properties, LivingEntity e, boolean handsOnly){
        if (e.getEquipment() != null) {
            if (!handsOnly){
                properties.setHelmet(e.getEquipment().getHelmet());
                properties.setChestplate(e.getEquipment().getChestplate());
                properties.setLeggings(e.getEquipment().getLeggings());
                properties.setBoots(e.getEquipment().getBoots());
            }
            properties.setMainHand(e.getEquipment().getItemInMainHand());
            properties.setOffHand(e.getEquipment().getItemInOffHand());
        }

        return properties;
    }
}

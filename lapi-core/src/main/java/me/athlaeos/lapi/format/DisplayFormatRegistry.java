package me.athlaeos.lapi.format;

import me.athlaeos.lapi.LoreAPIPlugin;
import me.athlaeos.lapi.configuration.ConfigManager;
import me.athlaeos.lapi.utils.Catch;
import me.athlaeos.lapi.utils.ItemUtils;
import me.athlaeos.lapi.utils.Reloadable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayFormatRegistry implements Reloadable {
    private static final NamespacedKey KEY_DISPLAY_FORMAT = new NamespacedKey(LoreAPIPlugin.getInstance(), "display_format");
    private static Map<String, DisplayFormat> registeredFormats = new HashMap<>();
    private static Map<Material, DisplayFormat> materialDefaultFormats = new HashMap<>();
    private static DisplayFormat defaultFormat = null;

    public static void load(){
        registeredFormats = new HashMap<>();
        materialDefaultFormats = new HashMap<>();
        defaultFormat = null;
        YamlConfiguration config = ConfigManager.getConfig("formats.yml").get();
        ConfigurationSection formatSection = config.getConfigurationSection("formats");
        if (formatSection != null) {
            for (String format : formatSection.getKeys(false)){
                String displayName = config.getString("formats." + format + ".display_name");
                List<String> lore = config.getStringList("formats." + format + ".lore");
                DisplayFormat displayFormat = new DisplayFormat(format, displayName, lore);
                if (format.equals("default")) defaultFormat = displayFormat;
                registerFormat(displayFormat);
            }
        }

        ConfigurationSection materialDefaultsSection = config.getConfigurationSection("material_defaults");
        if (materialDefaultsSection == null) return;
        for (String mat : materialDefaultsSection.getKeys(false)){
            Material material = Catch.catchOrElse(() -> Material.valueOf(mat), null, "formats.yml >>> " + mat + " is not a valid material!");
            if (material == null) continue;
            String formatString = config.getString("material_defaults." + mat);
            if (formatString == null) continue;
            DisplayFormat format = getFormat(formatString);
            if (format == null){
                LoreAPIPlugin.logWarning("formats.yml >>> " + mat + ": "+ formatString + " is not a valid format!");
                continue;
            }
            materialDefaultFormats.put(material, format);
        }
    }

    public static void registerFormat(DisplayFormat format){
        registeredFormats.put(format.getID(), format);
    }

    public static Map<String, DisplayFormat> getRegisteredFormats() {
        return Collections.unmodifiableMap(registeredFormats);
    }

    public static DisplayFormat getFormat(String id){
        return id == null ? defaultFormat : registeredFormats.getOrDefault(id, defaultFormat);
    }

    public static DisplayFormat getFormat(String id, Material def){
        if (def == null) return getFormat(id);
        DisplayFormat defFormat = materialDefaultFormats.getOrDefault(def, defaultFormat);
        return id == null ? defFormat : registeredFormats.getOrDefault(id, defFormat);
    }

    public static DisplayFormat getFormat(ItemMeta meta){
        Material stored = ItemUtils.getStoredType(meta);
        return getFormat(meta.getPersistentDataContainer().get(KEY_DISPLAY_FORMAT, PersistentDataType.STRING), stored);
    }

    public static void setFormat(ItemMeta meta, DisplayFormat format){
        if (format == null) meta.getPersistentDataContainer().remove(KEY_DISPLAY_FORMAT);
        else meta.getPersistentDataContainer().set(KEY_DISPLAY_FORMAT, PersistentDataType.STRING, format.getID());
    }

    @Override
    public void reload() {
        ConfigManager.getConfig("formats.yml").reload();
        load();
    }

    @Override
    public boolean canReloadAsync() {
        return true;
    }
}

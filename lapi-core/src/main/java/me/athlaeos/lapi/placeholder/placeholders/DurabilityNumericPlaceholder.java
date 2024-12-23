package me.athlaeos.lapi.placeholder.placeholders;

import me.athlaeos.lapi.LoreAPIPlugin;
import me.athlaeos.lapi.configuration.ConfigManager;
import me.athlaeos.lapi.placeholder.StringPlaceholder;
import me.athlaeos.lapi.utils.Catch;
import me.athlaeos.lapi.utils.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.TreeMap;

public class DurabilityNumericPlaceholder extends StringPlaceholder {
    private final String format;
    private final TreeMap<Double, String> colorMap = new TreeMap<>(Collections.reverseOrder());

    public DurabilityNumericPlaceholder(){
        LoreAPIPlugin.getInstance().saveConfig("placeholder_configs/item_durability.yml");
        YamlConfiguration config = ConfigManager.getConfig("placeholder_configs/item_durability.yml").get();
        format = config.getString("numeric_format");

        ConfigurationSection durabilityColorSection = config.getConfigurationSection("numeric_colors");
        if (durabilityColorSection == null) return;
        for (String level : durabilityColorSection.getKeys(true)){
            Double d = Catch.catchOrElse(() -> Double.parseDouble(level), -1D);
            if (d <= 0) continue;
            colorMap.put(d, config.getString("numeric_colors." + level));
        }
    }

    private String getColor(double fraction){
        String lastColor = "";
        for (Double d : colorMap.keySet()){
            if (fraction <= d) lastColor = colorMap.get(d);
            else break;
        }
        return lastColor;
    }

    @Override
    public String parse(Player player, ItemBuilder item) {
        int durability = LoreAPIPlugin.getNms().getDurability(item);
        int maxDurability = LoreAPIPlugin.getNms().getMaxDurability(item);
        double fraction = (double) durability / maxDurability;
        return format.replace("%color%", getColor(fraction))
                .replace("%durability%", String.valueOf(durability))
                .replace("%max_durability%", String.valueOf(maxDurability));
    }

    @Override
    public String getIdentifier() {
        return "lapi";
    }

    @Override
    public String getPlaceholder() {
        return "durability_numeric";
    }
}

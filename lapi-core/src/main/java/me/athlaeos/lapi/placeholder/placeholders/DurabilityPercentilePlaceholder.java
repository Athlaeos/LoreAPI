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

public class DurabilityPercentilePlaceholder extends StringPlaceholder {
    private final int precision;
    private final String format;
    private final TreeMap<Double, String> prefixMap = new TreeMap<>(Collections.reverseOrder());

    public DurabilityPercentilePlaceholder(){
        LoreAPIPlugin.getInstance().saveConfig("placeholder_configs/item_durability.yml");
        YamlConfiguration config = ConfigManager.getConfig("placeholder_configs/item_durability.yml").get();
        precision = config.getInt("percentile_precision");
        format = config.getString("percentile_format");

        ConfigurationSection durabilityColorSection = config.getConfigurationSection("precentile_colors");
        if (durabilityColorSection == null) return;
        for (String level : durabilityColorSection.getKeys(true)){
            Double d = Catch.catchOrElse(() -> Double.parseDouble(level), -1D);
            if (d <= 0) continue;
            prefixMap.put(d, config.getString("precentile_colors." + level));
        }
    }

    private String getColor(double fraction){
        String lastColor = "";
        for (Double d : prefixMap.keySet()){
            if (fraction <= d) lastColor = prefixMap.get(d);
            else break;
        }
        return lastColor;
    }

    @Override
    public String parse(Player player, ItemBuilder item) {
        String format = "%." + precision + "f";
        double fraction = (double) LoreAPIPlugin.getNms().getDurability(item) / LoreAPIPlugin.getNms().getMaxDurability(item);
        return this.format
                .replace("%color%", getColor(fraction))
                .replace("%durability%", String.format(format, fraction * 100));
    }

    @Override
    public String getIdentifier() {
        return "lapi";
    }

    @Override
    public String getPlaceholder() {
        return "durability_percentile";
    }
}

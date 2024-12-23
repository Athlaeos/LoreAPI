package me.athlaeos.lapi.placeholder.placeholders;

import me.athlaeos.lapi.LoreAPIPlugin;
import me.athlaeos.lapi.configuration.ConfigManager;
import me.athlaeos.lapi.placeholder.PlaceholderRegistry;
import me.athlaeos.lapi.placeholder.StringPlaceholder;
import me.athlaeos.lapi.utils.Catch;
import me.athlaeos.lapi.utils.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.TreeMap;

public class DurabilityBarPlaceholder extends StringPlaceholder {
    private final String format;
    private final String emptyColor;
    private final TreeMap<Double, String> colorMap = new TreeMap<>(Collections.reverseOrder());

    public DurabilityBarPlaceholder(){
        YamlConfiguration config = ConfigManager.getConfig("placeholder_configs/item_durability.yml").get();
        format = config.getString("bar_format");
        emptyColor = config.getString("bar_empty_color");

        ConfigurationSection durabilityColorSection = config.getConfigurationSection("bar_colors");
        if (durabilityColorSection == null) return;
        for (String level : durabilityColorSection.getKeys(true)){
            Double d = Catch.catchOrElse(() -> Double.parseDouble(level), -1D);
            if (d <= 0) continue;
            colorMap.put(d, config.getString("bar_colors." + level));
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
        double fraction = (double) LoreAPIPlugin.getNms().getDurability(item) / LoreAPIPlugin.getNms().getMaxDurability(item);
        String parsed = PlaceholderRegistry.parseString(player, item, format);
        int splitIndex = (int) Math.max(1, Math.min(parsed.length(), Math.floor(parsed.length() * fraction)));
        String coloredPart = parsed.substring(0, splitIndex);
        String emptyPart = parsed.substring(splitIndex);

        return String.format("%s%s%s%s", getColor(fraction), coloredPart, emptyColor, emptyPart);
    }

    @Override
    public String getIdentifier() {
        return "lapi";
    }

    @Override
    public String getPlaceholder() {
        return "durability_bar";
    }
}

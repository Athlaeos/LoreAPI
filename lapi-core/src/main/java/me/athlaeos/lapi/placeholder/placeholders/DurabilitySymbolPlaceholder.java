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

public class DurabilitySymbolPlaceholder extends StringPlaceholder {
    private final String format;
    private final TreeMap<Double, String> symbols = new TreeMap<>(Collections.reverseOrder());

    public DurabilitySymbolPlaceholder(){
        LoreAPIPlugin.getInstance().saveConfig("placeholder_configs/item_durability.yml");
        YamlConfiguration config = ConfigManager.getConfig("placeholder_configs/item_durability.yml").get();
        format = config.getString("symbol_format");

        ConfigurationSection durabilitySymbolSection = config.getConfigurationSection("symbols");
        if (durabilitySymbolSection == null) return;
        for (String level : durabilitySymbolSection.getKeys(true)){
            Double d = Catch.catchOrElse(() -> Double.parseDouble(level), -1D);
            if (d <= 0) continue;
            symbols.put(d, config.getString("symbols." + level));
        }
    }

    private String getSymbol(double fraction){
        String lastSymbol = "";
        for (Double d : symbols.keySet()){
            if (fraction <= d) lastSymbol = symbols.get(d);
            else break;
        }
        return lastSymbol;
    }

    @Override
    public String parse(Player player, ItemBuilder item) {
        double fraction = (double) LoreAPIPlugin.getNms().getDurability(item) / LoreAPIPlugin.getNms().getMaxDurability(item);
        return format.replace("%symbol%", getSymbol(fraction));
    }

    @Override
    public String getIdentifier() {
        return "lapi";
    }

    @Override
    public String getPlaceholder() {
        return "durability_symbol";
    }
}

package me.athlaeos.lapi.placeholder.placeholders;

import me.athlaeos.lapi.LoreAPIPlugin;
import me.athlaeos.lapi.configuration.ConfigManager;
import me.athlaeos.lapi.placeholder.ListPlaceholder;
import me.athlaeos.lapi.utils.ItemBuilder;
import me.athlaeos.lapi.utils.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LorePlaceholder extends ListPlaceholder {
    private final List<String> format;

    public LorePlaceholder(){
        LoreAPIPlugin.getInstance().saveConfig("placeholder_configs/item_lore.yml");
        YamlConfiguration config = ConfigManager.getConfig("placeholder_configs/item_lore.yml").get();
        format = config.getStringList("format");
    }

    @Override
    public List<String> parse(Player player, ItemBuilder item) {
        if (!item.getMeta().hasLore() || item.getMeta().getLore() == null || item.getMeta().getLore().isEmpty()) return new ArrayList<>();
        return StringUtils.setListPlaceholder(format, "%lore%", item.getMeta().getLore());
    }

    @Override
    public String getIdentifier() {
        return "lapi";
    }

    @Override
    public String getPlaceholder() {
        return "item_lore";
    }
}

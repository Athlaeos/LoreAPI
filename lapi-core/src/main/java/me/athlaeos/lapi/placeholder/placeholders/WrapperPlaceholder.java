package me.athlaeos.lapi.placeholder.placeholders;

import me.athlaeos.lapi.placeholder.ListPlaceholder;
import me.athlaeos.lapi.placeholder.PlaceholderRegistry;
import me.athlaeos.lapi.utils.ItemBuilder;
import org.bukkit.entity.Player;

import java.util.List;

public class WrapperPlaceholder extends ListPlaceholder {
    private final List<String> format;
    private final String placeholder;

    public WrapperPlaceholder(String placeholder, List<String> format){
        this.placeholder = placeholder;
        this.format = format;
    }

    @Override
    public List<String> parse(Player player, ItemBuilder item) {
        return PlaceholderRegistry.parseList(player, item, format);
    }

    @Override
    public String getIdentifier() {
        return "wrapper";
    }

    @Override
    public String getPlaceholder() {
        return placeholder;
    }
}

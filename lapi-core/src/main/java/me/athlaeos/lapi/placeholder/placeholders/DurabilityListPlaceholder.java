package me.athlaeos.lapi.placeholder.placeholders;

import me.athlaeos.lapi.LoreAPIPlugin;
import me.athlaeos.lapi.placeholder.StringPlaceholder;
import me.athlaeos.lapi.utils.ItemBuilder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DurabilityListPlaceholder extends ListWrapper{
    public DurabilityListPlaceholder(StringPlaceholder parentPlaceholder) {
        super(parentPlaceholder);
    }

    @Override
    protected boolean shouldInsert(Player player, ItemBuilder item) {
        return LoreAPIPlugin.getNms().getMaxDurability(item) > 0;
    }

    @Override
    public List<String> parse(Player player, ItemBuilder item) {
        return shouldInsert(player, item) ?
                new ArrayList<>(List.of(parentPlaceholder.parse(player, item))) :
                new ArrayList<>();
    }
}

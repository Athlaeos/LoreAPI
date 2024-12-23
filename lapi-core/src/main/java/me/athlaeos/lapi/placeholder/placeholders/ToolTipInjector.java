package me.athlaeos.lapi.placeholder.placeholders;

import me.athlaeos.lapi.LoreAPIPlugin;
import me.athlaeos.lapi.placeholder.ParameterizedPlaceholder;
import me.athlaeos.lapi.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ToolTipInjector implements ParameterizedPlaceholder {

    @Override
    public @NotNull String getIdentifier() {
        return "lapi_item_tooltip_style";
    }

    @Override
    public List<String> parseList(Player player, ItemBuilder item, @NotNull String params) {
        LoreAPIPlugin.getNms().setToolTipStyle(item, params);
        return new ArrayList<>();
    }
}

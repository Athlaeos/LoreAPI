package me.athlaeos.lapi.placeholder.placeholders;

import me.athlaeos.lapi.localization.Translator;
import me.athlaeos.lapi.placeholder.StringPlaceholder;
import me.athlaeos.lapi.utils.ItemBuilder;
import org.bukkit.entity.Player;

public class DisplayNamePlaceholder extends StringPlaceholder {
    @Override
    public String parse(Player player, ItemBuilder item) {
        return item.getMeta().hasDisplayName() ? item.getMeta().getDisplayName() : Translator.getMaterialTranslation(item.getItem().getType().toString());
    }

    @Override
    public String getIdentifier() {
        return "lapi";
    }

    @Override
    public String getPlaceholder() {
        return "item_name";
    }
}

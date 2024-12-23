package me.athlaeos.lapi.placeholder.placeholders;

import me.athlaeos.lapi.LoreAPIPlugin;
import me.athlaeos.lapi.placeholder.ListPlaceholder;
import me.athlaeos.lapi.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentGlintInjector extends ListPlaceholder {

    @Override
    public @NotNull String getIdentifier() {
        return "lapi";
    }

    @Override
    public String getPlaceholder() {
        return "item_enchantment_glint";
    }

    @Override
    public List<String> parse(Player player, ItemBuilder item) {
        LoreAPIPlugin.getNms().setEnchantmentGlint(item, true);
        return new ArrayList<>();
    }
}

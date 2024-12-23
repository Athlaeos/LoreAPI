package me.athlaeos.lapi.placeholder.placeholders.attribute;

import me.athlaeos.lapi.placeholder.StringPlaceholder;
import me.athlaeos.lapi.placeholder.placeholders.ListWrapper;
import me.athlaeos.lapi.utils.DefaultItemProperties;
import me.athlaeos.lapi.utils.ItemBuilder;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AttributeListPlaceholder extends ListWrapper {
    private final Attribute attribute;
    public AttributeListPlaceholder(StringPlaceholder parent, Attribute attribute) {
        super(parent);
        this.attribute = attribute;
    }

    @Override
    protected boolean shouldInsert(Player player, ItemBuilder item) {
        Collection<AttributeModifier> modifiers = item.getMeta().getAttributeModifiers(attribute);
        return (modifiers != null && !modifiers.isEmpty()) || DefaultItemProperties.getDefaultModifier(attribute, item.getItem().getType()) != null;
    }

    @Override
    public List<String> parse(Player player, ItemBuilder item) {
        return shouldInsert(player, item) ? new ArrayList<>(List.of(parentPlaceholder.parse(player, item))) : new ArrayList<>();
    }
}

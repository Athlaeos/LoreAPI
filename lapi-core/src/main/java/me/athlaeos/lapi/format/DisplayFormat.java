package me.athlaeos.lapi.format;

import me.athlaeos.lapi.LoreAPIPlugin;
import me.athlaeos.lapi.placeholder.PlaceholderRegistry;
import me.athlaeos.lapi.utils.ItemBuilder;
import me.athlaeos.lapi.utils.StringUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DisplayFormat {
    private final String id;
    private String displayNameFormat;
    private List<String> loreFormat;

    public DisplayFormat(String id, String displayNameFormat, List<String> loreFormat){
        this.id = id;
        this.displayNameFormat = displayNameFormat;
        this.loreFormat = loreFormat;
    }

    public String getID() {
        return id;
    }

    public String getDisplayNameFormat() {
        return displayNameFormat;
    }

    public List<String> getLoreFormat() {
        return loreFormat;
    }

    public void setDisplayNameFormat(String displayNameFormat) {
        this.displayNameFormat = displayNameFormat;
    }

    public void setLoreFormat(List<String> loreFormat) {
        this.loreFormat = loreFormat;
    }

    public void convert(ItemBuilder builder, Player observer){
        List<String> lore = getLoreFormat() == null ? (builder.getMeta().hasLore() ? builder.getMeta().getLore() : null) : new ArrayList<>(getLoreFormat());
        if (lore != null) {
            lore = PlaceholderRegistry.parseList(observer, builder, lore);
        }
        lore = StringUtils.standardCenter(lore);
        builder.lore(lore);

        String displayName = getDisplayNameFormat() == null ? (builder.getMeta().hasDisplayName() ? builder.getMeta().getDisplayName() : null) : getDisplayNameFormat();
        if (displayName != null) {
            displayName = PlaceholderRegistry.parseString(observer, builder, displayName);
        }
        if (displayName != null && lore != null && displayName.startsWith("#c:")) {
            List<String> asList = new ArrayList<>(List.of(displayName));
            asList = StringUtils.center(asList, s -> s.startsWith("#c:"), s -> s.replaceAll("#c:", ""), StringUtils.getMaxStringLength(lore, s -> s.replaceAll("#c:", "")));
            if (!asList.isEmpty()) displayName = asList.getFirst(); // Should never be empty at this point, but just to be sure
        }
        builder.name(displayName);
    }
}

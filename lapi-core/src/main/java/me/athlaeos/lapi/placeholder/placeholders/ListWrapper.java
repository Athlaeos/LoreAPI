package me.athlaeos.lapi.placeholder.placeholders;

import me.athlaeos.lapi.placeholder.ListPlaceholder;
import me.athlaeos.lapi.placeholder.StringPlaceholder;
import me.athlaeos.lapi.utils.ItemBuilder;
import org.bukkit.entity.Player;

public abstract class ListWrapper extends ListPlaceholder {
    protected StringPlaceholder parentPlaceholder;

    public ListWrapper(StringPlaceholder parentPlaceholder){
        this.parentPlaceholder = parentPlaceholder;
    }

    @Override
    public String getIdentifier() {
        return parentPlaceholder.getIdentifier() + "_list";
    }

    @Override
    public String getPlaceholder() {
        return parentPlaceholder.getPlaceholder();
    }

    protected abstract boolean shouldInsert(Player player, ItemBuilder item);

    public void register(boolean registerParent){
        if (registerParent) parentPlaceholder.register();
        super.register();
    }
}

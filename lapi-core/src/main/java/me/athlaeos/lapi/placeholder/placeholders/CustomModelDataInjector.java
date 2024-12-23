package me.athlaeos.lapi.placeholder.placeholders;

import me.athlaeos.lapi.LoreAPIPlugin;
import me.athlaeos.lapi.placeholder.ParameterizedPlaceholder;
import me.athlaeos.lapi.utils.Catch;
import me.athlaeos.lapi.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomModelDataInjector implements ParameterizedPlaceholder {

    @Override
    public @NotNull String getIdentifier() {
        return "lapi_custom_model_data";
    }

    @Override
    public List<String> parseList(Player player, ItemBuilder item, @NotNull String params) {
        Integer CMD = Catch.catchOrElse(() -> Integer.parseInt(params), null);
        if (CMD != null) item.data(CMD);
        return new ArrayList<>();
    }
}

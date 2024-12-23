package me.athlaeos.lapi.commands.implementations;

import me.athlaeos.lapi.commands.Command;
import me.athlaeos.lapi.format.DisplayFormat;
import me.athlaeos.lapi.format.DisplayFormatRegistry;
import me.athlaeos.lapi.localization.Translator;
import me.athlaeos.lapi.utils.ItemUtils;
import me.athlaeos.lapi.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class FormatSetCommand implements Command {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) return false;
        Collection<Player> targets = args.length > 2 ? Utils.selectPlayers(sender, args[2]) : (sender instanceof Player p ? Set.of(p) : new HashSet<>());
        if (targets.isEmpty()) {
            Utils.sendMessage(sender, Translator.getTranslation("error_command_players_only"));
            return true;
        }

        String formatID = args[1];
        DisplayFormat format = DisplayFormatRegistry.getFormat(formatID);
        if (format == null){
            Utils.sendMessage(sender, Translator.getTranslation("error_command_invalid_format"));
            return true;
        }
        for (Player player : targets){
            ItemStack held = player.getInventory().getItemInMainHand();
            if (ItemUtils.isEmpty(held)){
                Utils.sendMessage(player, Translator.getTranslation("error_command_held_item_required"));
                return true;
            }
            ItemMeta meta = held.getItemMeta();
            if (meta == null) return true; // This should never really happen
            DisplayFormatRegistry.setFormat(meta, format);
            held.setItemMeta(meta);
            Utils.sendMessage(player, Translator.getTranslation("command_status_item_format_set"));
        }
        return true;
    }

    @Override
    public String getFailureMessage(String[] args) {
        return "/lapi setformat [format] <targets>";
    }

    @Override
    public String getDescription() {
        return Translator.getTranslation("command_description_format");
    }

    @Override
    public String getCommand() {
        return "/lapi setformat [format] <targets>";
    }

    @Override
    public String[] getRequiredPermissions() {
        return new String[]{"loreapi.format.set"};
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("loreapi.format.set");
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        if (args.length == 2) return new ArrayList<>(DisplayFormatRegistry.getRegisteredFormats().keySet());
        if (args.length == 3) return null;
        return List.of();
    }
}

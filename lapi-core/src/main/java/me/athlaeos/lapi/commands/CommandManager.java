package me.athlaeos.lapi.commands;

import me.athlaeos.lapi.LoreAPIPlugin;
import me.athlaeos.lapi.commands.implementations.FormatSetCommand;
import me.athlaeos.lapi.commands.implementations.HelpCommand;
import me.athlaeos.lapi.localization.Translator;
import me.athlaeos.lapi.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager implements TabExecutor {
	private static final Map<String, Command> commands = new HashMap<>();

	public CommandManager() {
		commands.put("help", new HelpCommand());
		commands.put("setformat", new FormatSetCommand());
	}

	public static Map<String, Command> getCommands() {
		return commands;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command cmd, @NotNull String name, String[] args) {
		if (args.length == 0) {
			for (String s : Translator.getListTranslation("no_command")){
				Utils.sendMessage(sender, s.replace("%version%", LoreAPIPlugin.getInstance().getDescription().getVersion()));
			}
			return true;
		}

		Command command = commands.get(args[0].toLowerCase(java.util.Locale.US));
		if (command == null) {
			Utils.sendMessage(sender, Utils.chat(Translator.getTranslation("error_command_invalid_command")));
			return true;
		}

		if (!command.hasPermission(sender)){
			Utils.sendMessage(sender, Utils.chat(Translator.getTranslation("error_command_no_permission")));
			return true;
		}

		if (!command.execute(sender, args)) Utils.sendMessage(sender, Utils.chat(command.getFailureMessage(args)));
		return true;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command cmd, @NotNull String name, String[] args) {
		if (args.length == 1) {
			List<String> allowedCommands = new ArrayList<>();
			for (String arg : commands.keySet()) {
				Command command = commands.get(arg);
				if (command.hasPermission(sender)) {
					allowedCommands.add(arg);
				}
			}
			return allowedCommands;
		} else if (args.length > 1) {
			Command command = commands.get(args[0]);
			if (command == null || !command.hasPermission(sender)) return Command.noSubcommandArgs();
			return command.getSubcommandArgs(sender, args);
		}
		return null;
	}
}

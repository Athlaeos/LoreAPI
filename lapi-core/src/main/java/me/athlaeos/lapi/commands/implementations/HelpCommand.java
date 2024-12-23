package me.athlaeos.lapi.commands.implementations;

import me.athlaeos.lapi.commands.Command;
import me.athlaeos.lapi.commands.CommandManager;
import me.athlaeos.lapi.localization.Translator;
import me.athlaeos.lapi.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HelpCommand implements Command {
	private static final int MAX_COMMANDS_PER_PAGE = 3;

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Map<Integer, List<String>> pages;
		List<String> helpLines = new ArrayList<>();
		
		for (Command c : CommandManager.getCommands().values()) {
			if (c.hasPermission(sender)) {
				for (String line : Translator.getListTranslation("command_format_help")){
					helpLines.add(line
							.replace("%description%", c.getDescription())
							.replace("%permissions%", String.join("|", c.getRequiredPermissions()))
							.replace("%command%", c.getCommand()));
				}
			}
		}

		pages = Utils.paginate(Translator.getListTranslation("command_format_help").size() * MAX_COMMANDS_PER_PAGE, helpLines);
		
		if (pages.isEmpty()) return true;

		int page = 0;
		if (args.length >= 2){
			try {
				page = Integer.parseInt(args[1]);
			} catch (NumberFormatException nfe) {
				Utils.sendMessage(sender, Utils.chat(Translator.getTranslation("error_command_invalid_number")));
				return true;
			}
		}

		for (String line : pages.get(Math.max(0, Math.min(pages.size() - 1, page)))) {
			sender.sendMessage(Utils.chat(line));
		}
		Utils.chat("&8&m                                             ");
		sender.sendMessage(Utils.chat(String.format("&8[&e1&8/&e%s&8]", pages.size())));
		return true;
	}

	@Override
	public String getFailureMessage(String[] args) {
		return "/lapi help <page>";
	}

	@Override
	public String getCommand() {
		return "/lapi help <page>";
	}

	@Override
	public String[] getRequiredPermissions() {
		return new String[]{"loreapi.help"};
	}

	@Override
	public boolean hasPermission(CommandSender sender) {
		return sender.hasPermission("loreapi.help");
	}

	@Override
	public String getDescription() {
		return Translator.getTranslation("description_command_help");
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2) return List.of("1", "2", "3", "...");
		return Command.noSubcommandArgs();
	}
}

package lv.kaneps.oitc.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HelpCommand extends SubCommand
{
	public HelpCommand(BaseCommand base, Player p)
	{
		super(base, p, "oitc.help");
	}

	@Override
	public boolean onCommand()
	{
		if(!p.hasPermission("oitc.help.advanced"))
			return false;

		p.sendMessage(ChatColor.DARK_GREEN + "Usage:");
		p.sendMessage(ChatColor.DARK_AQUA + " /oitc (join|leave)");
		p.sendMessage(ChatColor.DARK_AQUA + " /oitc vote <arenaName>");
		p.sendMessage(ChatColor.DARK_AQUA + " /oitc arena list" + ChatColor.RESET + " or " + ChatColor.DARK_AQUA + "/oitc (arenas|list)");
		p.sendMessage(ChatColor.DARK_AQUA + " /oitc arena (create|remove) <arenaName>");
		p.sendMessage(ChatColor.DARK_AQUA + " /oitc arena displayname <arenaName> <displayName>");
		p.sendMessage(ChatColor.DARK_AQUA + " /oitc arena addspawn <arenaName>");
		p.sendMessage(ChatColor.DARK_AQUA + " /oitc arena removespawn <arenaName>");
		p.sendMessage(ChatColor.DARK_AQUA + " /oitc arena (validate|invalidate) <arenaName>");
		p.sendMessage(ChatColor.DARK_AQUA + " /oitc arena select <arenaName>");
		p.sendMessage(ChatColor.DARK_AQUA + " /oitc (addspawn|removespawn)");

		return true;
	}
}

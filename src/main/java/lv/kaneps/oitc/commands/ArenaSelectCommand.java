package lv.kaneps.oitc.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ArenaSelectCommand extends SubCommand
{
	protected final String arenaName;

	public ArenaSelectCommand(BaseCommand base, Player p, String arenaName)
	{
		super(base, p, "oitc.arena.select");
		this.arenaName = arenaName;
	}

	@Override
	public boolean onCommand()
	{
		if(arenaName == null || arenaName.isEmpty())
		{
			BaseCommand.arenaNotFound(p.getPlayer(), ChatColor.DARK_RED + "null");
			return true;
		}

		if (base.oitc.arenas().containsArena(arenaName))
		{
			p.sendMessage(ChatColor.DARK_GREEN + "You selected '" + ChatColor.AQUA + arenaName + ChatColor.DARK_GREEN + "'!");
			base.selectedArenas.put(p.getName(), arenaName);
		}
		else
		{
			p.sendMessage(ChatColor.RED + "Arena with name '" + ChatColor.AQUA + arenaName + ChatColor.RED + "' not found!");
			base.selectedArenas.remove(p.getName());
		}
		return true;
	}
}

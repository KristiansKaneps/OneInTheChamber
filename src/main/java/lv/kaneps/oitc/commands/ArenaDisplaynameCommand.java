package lv.kaneps.oitc.commands;

import lv.kaneps.oitc.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ArenaDisplaynameCommand extends SubCommand
{
	protected final String arenaName, arenaDisplayname;

	public ArenaDisplaynameCommand(BaseCommand base, Player p, String arenaName, String arenaDisplayname)
	{
		super(base, p, "oitc.arena.displayname", true);
		this.arenaName = arenaName;
		this.arenaDisplayname = arenaDisplayname;
	}

	@Override
	public boolean onCommand()
	{
		if(arenaName == null || arenaName.isEmpty())
		{
			BaseCommand.arenaNotFound(p.getPlayer(), ChatColor.DARK_RED + "null");
			return true;
		}

		if (!base.oitc.arenas().containsArena(arenaName))
		{
			p.sendMessage(ChatColor.RED + "Arena with name '" + ChatColor.AQUA + arenaName + ChatColor.RED + "' not found!");
			base.selectedArenas.remove(p.getName());
			return true;
		}

		Arena arena = base.oitc.arenas().getArena(arenaName);
		arena.setDisplayName(arenaDisplayname);
		base.oitc.arenas().save();

		p.sendMessage(ChatColor.DARK_GREEN + "'" + ChatColor.AQUA + arenaName + ChatColor.DARK_GREEN + "' displayname changed to '" + ChatColor.RESET + arenaDisplayname + ChatColor.DARK_GREEN + "'!");

		return true;
	}
}

package lv.kaneps.oitc.commands;

import lv.kaneps.oitc.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ArenaCreateCommand extends SubCommand
{
	protected final String arenaName;

	public ArenaCreateCommand(BaseCommand base, Player p, String arenaName)
	{
		super(base, p, "oitc.arena.create");
		this.arenaName = arenaName;
	}

	@Override
	public boolean onCommand()
	{
		if (base.oitc.arenas().containsArena(arenaName))
		{
			p.sendMessage(ChatColor.RED + "Arena with this name ('" + ChatColor.AQUA + arenaName + ChatColor.RED + "') already exists!");
			return true;
		}

		Arena arena = new Arena(arenaName);

		base.oitc.arenas().addArena(arena);
		base.oitc.arenas().save();

		synchronized (base.selectedArenas)
		{
			base.selectedArenas.put(p.getName(), arenaName);
		}

		p.sendMessage(ChatColor.DARK_GREEN + "Arena '" + ChatColor.AQUA + arenaName + ChatColor.DARK_GREEN + "' created!");

		return true;
	}
}

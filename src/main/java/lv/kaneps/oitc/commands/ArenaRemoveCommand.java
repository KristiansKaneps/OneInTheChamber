package lv.kaneps.oitc.commands;

import lv.kaneps.oitc.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ArenaRemoveCommand extends SubCommand
{
	protected final String arenaName;

	public ArenaRemoveCommand(BaseCommand base, Player p, String arenaName)
	{
		super(base, p, "oitc.arena.remove", true);
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

		if (!base.oitc.arenas().containsArena(arenaName))
		{
			p.sendMessage(ChatColor.RED + "Arena with name '" + ChatColor.AQUA + arenaName + ChatColor.RED + "' doesn't exist!");
			return true;
		}

		base.oitc.arenas().removeArena(arenaName);
		base.oitc.arenas().save();

		synchronized (base.selectedArenas)
		{
			if(base.selectedArenas.containsKey(p.getName()))
			{
				String selArenaName = base.selectedArenas.get(p.getName());
				if(arenaName.equals(selArenaName))
					base.selectedArenas.remove(p.getName());
			}
		}

		p.sendMessage(ChatColor.DARK_GREEN + "Arena '" + ChatColor.AQUA + arenaName + ChatColor.DARK_GREEN + "' deleted!");

		return true;
	}
}

package lv.kaneps.oitc.commands;

import lv.kaneps.oitc.arena.ArenaStorage;
import lv.kaneps.oitc.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RemoveSpawnCommand extends SubCommand
{
	protected final String arenaName;

	public RemoveSpawnCommand(BaseCommand base, Player p, String arenaName)
	{
		super(base, p, "oitc.arena.spawn.remove", true);
		this.arenaName = arenaName;
	}

	@Override
	public boolean onCommand()
	{
		ArenaStorage arenas = base.oitc.arenas();

		if(arenaName == null || arenaName.isEmpty())
		{
			BaseCommand.arenaNotFound(p.getPlayer(), ChatColor.DARK_RED + "null");
			return true;
		}

		if(!arenas.containsArena(arenaName))
		{
			BaseCommand.arenaNotFound(p.getPlayer(), arenaName);
			return true;
		}

		Arena arena = arenas.getArena(arenaName);
		Location removedLoc = arena.removeSpawnInRadius(p.getLocation(), base.oitc.settings().getRemoveArenaSpawnRadius());
		arenas.save();

		if(removedLoc != null)
			BaseCommand.success(
					p.getPlayer(),
					"Spawn point (" + ChatColor.AQUA +
					"w=" + removedLoc.getWorld().getName() + ChatColor.DARK_GREEN + "; " + ChatColor.AQUA +
					"x=" + decimal(removedLoc.getX()) + ChatColor.DARK_GREEN + "; " + ChatColor.AQUA +
					"y=" + decimal(removedLoc.getY()) + ChatColor.DARK_GREEN + "; " + ChatColor.AQUA +
					"z=" + decimal(removedLoc.getZ()) + ChatColor.DARK_GREEN +
					") removed from '" + ChatColor.AQUA + arenaName + ChatColor.DARK_GREEN + "'!"
			);
		else
			BaseCommand.error(p.getPlayer(), "Spawn point not found at this location for '" + ChatColor.AQUA + arenaName + ChatColor.RED + "'!");

		return true;
	}

	private static String decimal(double value)
	{
		return String.format("%.2f", value);
	}
}

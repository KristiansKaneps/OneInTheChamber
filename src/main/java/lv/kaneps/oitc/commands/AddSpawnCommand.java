package lv.kaneps.oitc.commands;

import lv.kaneps.oitc.arena.ArenaStorage;
import lv.kaneps.oitc.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AddSpawnCommand extends SubCommand
{
	protected final String arenaName;

	public AddSpawnCommand(BaseCommand base, Player p, String arenaName)
	{
		super(base, p, "oitc.arena.spawn.add", true);
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
		arena.addSpawn(p.getLocation());
		arenas.save();

		BaseCommand.success(p.getPlayer(), "Spawn point added to '" + ChatColor.AQUA + arenaName + ChatColor.DARK_GREEN + "'!");

		return true;
	}
}

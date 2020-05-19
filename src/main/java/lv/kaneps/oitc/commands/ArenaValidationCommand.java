package lv.kaneps.oitc.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ArenaValidationCommand extends SubCommand
{
	protected final String arenaName;
	protected final boolean valid;

	public ArenaValidationCommand(BaseCommand base, Player p, String arenaName, boolean valid)
	{
		super(base, p, "oitc.arena.validate", true);
		this.arenaName = arenaName;
		this.valid = valid;
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

		if(valid) base.oitc.arenas().getArena(arenaName).validate();
		else base.oitc.arenas().getArena(arenaName).invalidate();
		base.oitc.arenas().save();

		p.sendMessage(ChatColor.DARK_GREEN + "You " + (valid ? ChatColor.GREEN + "validated" : ChatColor.RED + "invalidated") + ChatColor.DARK_GREEN + " '" + ChatColor.AQUA + arenaName + ChatColor.DARK_GREEN + "'!");

		return true;
	}
}

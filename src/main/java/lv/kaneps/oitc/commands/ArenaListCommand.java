package lv.kaneps.oitc.commands;

import lv.kaneps.oitc.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ArenaListCommand extends SubCommand
{
	public ArenaListCommand(BaseCommand base, Player p)
	{
		super(base, p, "oitc.arena.list");
	}

	public boolean onCommand()
	{
		boolean listAll = p.hasPermission("oitc.arena.listall");
		Collection<Arena> arenas = base.oitc.arenas().getArenas();

		p.sendMessage(ChatColor.DARK_GREEN + "Arenas: ");
		if(listAll) p.sendMessage(ChatColor.DARK_GREEN + "--- " + ChatColor.AQUA + "Playable" + ChatColor.DARK_GREEN + " ---");

		StringBuffer sb = new StringBuffer();
		sb.append(ChatColor.AQUA);
		for(Arena arena : arenas)
			if(arena.isPlayable())
			{
				sb.append(arena.getName());
				sb.append(" ");
			}
		p.sendMessage(sb.toString().trim());

		if(listAll)
		{
			p.sendMessage(ChatColor.DARK_GREEN + "--- " + ChatColor.DARK_AQUA + "[Invalid]" + ChatColor.DARK_GREEN + " / " + ChatColor.AQUA + "[Valid]" + ChatColor.DARK_GREEN + " ---");

			sb = new StringBuffer();
			for (Arena arena : arenas)
			{
				sb.append(arena.isValid() ? ChatColor.AQUA : ChatColor.DARK_AQUA);
				sb.append(arena.getName());
				sb.append(" ");
			}
			p.sendMessage(sb.toString().trim());

			if(base.selectedArenas.containsKey(p.getName()))
			{
				String selectedArenaName = base.selectedArenas.get(p.getName());
				if(selectedArenaName != null && !selectedArenaName.isEmpty())
				{
					Arena selectedArena = base.oitc.arenas().getArena(selectedArenaName);
					p.sendMessage(ChatColor.DARK_GREEN + "Selected arena: " + (selectedArena.isValid() ? ChatColor.AQUA : ChatColor.DARK_AQUA) + selectedArenaName);
				}
			}
		}

		return true;
	}
}

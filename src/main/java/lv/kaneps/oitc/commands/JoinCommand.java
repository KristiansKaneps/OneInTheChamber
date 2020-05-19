package lv.kaneps.oitc.commands;

import lv.kaneps.oitc.match.MatchManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class JoinCommand extends SubCommand
{
	public JoinCommand(BaseCommand base, Player p)
	{
		super(base, p, "oitc.join", false);
	}

	@Override
	public boolean onCommand()
	{
		if(p.isInMatch())
		{
			p.sendMessage(ChatColor.RED + "You are already in a match!");
			return true;
		}

		MatchManager manager = base.oitc.matchManager();
		manager.queue().enqueue(p);

		p.sendMessage(ChatColor.GREEN + "You have been placed in a queue.");

		return true;
	}
}

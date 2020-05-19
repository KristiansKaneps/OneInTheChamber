package lv.kaneps.oitc.commands;

import lv.kaneps.oitc.match.MatchManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LeaveCommand extends SubCommand
{
	public LeaveCommand(BaseCommand base, Player p)
	{
		super(base, p, "oitc.leave");
	}

	@Override
	public boolean onCommand()
	{
		MatchManager manager = base.oitc.matchManager();

		if(!p.isInMatch())
		{
			if(manager.queue().contains(p))
			{
				manager.queue().remove(p);
				p.sendMessage(ChatColor.GREEN + "You are no longer in queue.");
				return true;
			}

			p.sendMessage(ChatColor.RED + "You are not in a match!");
			return true;
		}

		manager.getMatch(p.getMatchId()).removePlayer(p);
		p.sendMessage(ChatColor.GREEN + "You left this match.");

		return true;
	}
}

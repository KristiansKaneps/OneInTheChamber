package lv.kaneps.oitc.commands;

import lv.kaneps.oitc.arena.Arena;
import lv.kaneps.oitc.arena.ArenaStorage;
import lv.kaneps.oitc.arena.ArenaVote;
import lv.kaneps.oitc.match.Match;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VoteCommand extends SubCommand
{
	protected final String arenaName;

	public VoteCommand(BaseCommand base, Player p, String arenaName)
	{
		super(base, p, "oitc.vote");
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

		if(!p.isInMatch())
		{
			p.sendMessage(ChatColor.RED + "You must be in a match to vote.");
			return true;
		}

		if(p.isPlaying())
		{
			p.sendMessage(ChatColor.RED + "You can't vote while you are playing.");
			return true;
		}

		Match match = base.oitc.matchManager().getMatch(p.getMatchId());
		ArenaStorage arenas = base.oitc.arenas();

		if(!match.isInVotePeriod())
		{
			p.sendMessage(ChatColor.RED + "You can't vote right now.");
			return true;
		}

		if(arenas.getPlayableArenaCount() <= 1)
		{
			p.sendMessage(ChatColor.RED + "There are not enough playable arenas to vote for.");
			return true;
		}

		if(!arenas.containsArena(arenaName))
		{
			BaseCommand.arenaNotFound(p.getPlayer(), arenaName);
			return true;
		}

		Arena arena = arenas.getArena(arenaName);

		if(!arena.isPlayable())
		{
			p.sendMessage(ChatColor.RED + "'" + ChatColor.DARK_AQUA + arenaName + ChatColor.RED + "' is not yet ready to be playable.");
			return true;
		}

		ArenaVote votes = match.votes();
		votes.addVote(p, arenaName);

		p.sendMessage(ChatColor.DARK_GREEN + "You voted for '" + ChatColor.AQUA + arenaName + ChatColor.DARK_GREEN + "'.");

		return true;
	}
}

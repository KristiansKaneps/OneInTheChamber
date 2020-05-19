package lv.kaneps.oitc.arena;

import lv.kaneps.oitc.match.Match;
import lv.kaneps.oitc.player.OITCPlayer;

import java.util.*;

public class ArenaVote
{
	protected final Map<String, Integer> arenaVotes = new HashMap<>();
	protected final Map<String, String> votedPlayers = new HashMap<>();

	protected final Match match;

	public ArenaVote(Match match)
	{
		this.match = match;
	}

	public synchronized boolean hasAnyoneVoted()
	{
		return arenaVotes.size() > 0;
	}

	public synchronized void addVote(OITCPlayer p, String arenaName)
	{
		if(votedPlayers.containsKey(p.getName()))
		{
			String pArenaName = votedPlayers.get(p.getName());
			removeVote(pArenaName);
		}

		votedPlayers.put(p.getName(), arenaName);

		if(arenaVotes.containsKey(arenaName))
		{
			arenaVotes.put(arenaName, arenaVotes.get(arenaName) + 1);
		}
		else
			arenaVotes.put(arenaName, 1);
	}

	public synchronized void removeVote(String arenaName)
	{
		if(arenaVotes.containsKey(arenaName))
		{
			int val = Math.max(0, arenaVotes.get(arenaName) - 1);
			if(val > 0) arenaVotes.put(arenaName, val);
			else arenaVotes.remove(arenaName);
		}
	}

	public synchronized int getVotes(String arenaName)
	{
		return arenaVotes.getOrDefault(arenaName, 0);
	}

	public synchronized Map<String, Integer> getAllVotes()
	{
		return arenaVotes;
	}

	/**
	 * [arena_1 => 4 votes]
	 * [arena_2 => 6 votes]
	 * [arena_3 => 1 vote]
	 * gets sorted as:
	 * [arena_2; arena_1; arena_3]
	 *
	 * @return arena list which is sorted by votes
	 */
	@SuppressWarnings("unchecked")
	public synchronized List<String> getSortedList()
	{
		Object[] entries = arenaVotes.entrySet().toArray();
		Arrays.sort(entries, (o1, o2) -> ((Map.Entry<String, Integer>) o2).getValue().compareTo(((Map.Entry<String, Integer>) o1).getValue()));
		List<String> sortedArenas = new ArrayList<>(entries.length);
		for(Object e : entries)
			sortedArenas.add(((Map.Entry<String, Integer>) e).getKey());
		return sortedArenas;
	}

	public synchronized void clear()
	{
		arenaVotes.clear();
		votedPlayers.clear();
	}
}

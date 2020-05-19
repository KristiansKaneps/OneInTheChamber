package lv.kaneps.oitc.match;

import lv.kaneps.oitc.OITC;
import lv.kaneps.oitc.player.OITCPlayer;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

import static lv.kaneps.oitc.match.Match.freeId;

public class MatchManager implements PlayerQueue.Listener
{
	protected final Map<Integer, Match> matches;
	protected final Map<Integer, BukkitTask> startTasks;

	protected final PlayerQueue queue;

	protected final OITC oitc;

	public MatchManager(OITC oitc)
	{
		this.oitc = oitc;
		matches = new HashMap<>();
		startTasks = new HashMap<>();
		queue = new PlayerQueue(this);
	}

	public PlayerQueue queue()
	{
		return queue;
	}

	public void stopAll()
	{
		synchronized (startTasks)
		{
			for (Map.Entry<Integer, BukkitTask> e : startTasks.entrySet())
			{
				BukkitTask task = e.getValue();
				if (!task.isCancelled())
					task.cancel();
			}
		}
		synchronized (matches)
		{
			for (Map.Entry<Integer, Match> e : matches.entrySet())
			{
				Match m = e.getValue();
				if (!m.hasEnded())
					m.stop();
			}
		}
	}

	public Match getMatch(int id)
	{
		synchronized (matches)
		{
			return matches.get(id);
		}
	}

	public BukkitTask getMatchStartTask(int id)
	{
		synchronized (startTasks)
		{
			return startTasks.get(id);
		}
	}

	protected Match createMatch()
	{
		Match match = new Match(this);
		synchronized (matches) { matches.put(match.getMatchId(), match); }
		return match;
	}

	protected void removeMatch(int matchId)
	{
		synchronized (matches) { matches.remove((Integer) matchId); }
		freeId(matchId);
	}

	protected void removeStartTask(int id)
	{
		synchronized (startTasks) { startTasks.remove(id); }
	}

	@Override
	public void onPlayerEnqueue(OITCPlayer player)
	{
		int minPlayersForMatch = oitc.settings().getMinPlayersForMatch(), maxPlayersForMatch = oitc.settings().getMaxPlayersPerMatch();

		synchronized(matches)
		{
			outer: for(Map.Entry<Integer, Match> e : matches.entrySet())
			{
				Match match = e.getValue();
				if(match.hasStarted()) continue;
				int freeSlotCount = match.getFreeSlots();
				for(int i = 0; i < freeSlotCount; i++)
				{
					OITCPlayer p = queue.dequeue();
					if(p != null)
					{
						match.addPlayer(p);
						if(match.getFreeSlots() == 0)
						{
							BukkitTask task = getMatchStartTask(match.getMatchId());
							if(task != null && !task.isCancelled())
							{
								task.cancel();
								removeStartTask(match.getMatchId());
								match.start();
							}
						}
						continue;
					}
					break outer;
				}
			}
		}

		int playerCount = queue.size();

		if(playerCount < minPlayersForMatch)
			return;

		Match match = createMatch();
		OITCPlayer p;
		for(int i = 0; (i < maxPlayersForMatch) && ((p = queue.dequeue()) != null); i++)
		{
			match.addPlayer(p);
		}

		synchronized (startTasks)
		{
			startTasks.put(match.getMatchId(), oitc.getServer().getScheduler().runTaskLater(oitc, () -> {
				removeStartTask(match.getMatchId());
				match.start();
				}, oitc.settings().getMatchForceStartTimeInSeconds() * 20 /* {forceStartTime} * {ticks per second} */));
		}
	}

	@Override
	public void onPlayerDequeue(OITCPlayer player)
	{

	}

	@Override
	public void onPlayerRemoved(OITCPlayer player)
	{

	}
}

package lv.kaneps.oitc.match;

import lv.kaneps.oitc.arena.Arena;
import lv.kaneps.oitc.arena.ArenaVote;
import lv.kaneps.oitc.player.ArenaPlayerState;
import lv.kaneps.oitc.player.OITCPlayer;
import lv.kaneps.oitc.scoreboard.Scoreboard;
import lv.kaneps.oitc.scoreboard.ScoreboardBuilder;
import lv.kaneps.oitc.scoreboard.ScoreboardEntry;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Match implements Runnable
{
	private static final AtomicInteger GEN_ID = new AtomicInteger(0);
	private static final List<Integer> USED_GEN_IDS = new ArrayList<>();
	protected static int generateId()
	{
		if(USED_GEN_IDS.contains((Integer) GEN_ID.get()) || GEN_ID.get() == -1)
		{
			GEN_ID.incrementAndGet();
			return generateId();
		}
		USED_GEN_IDS.add(GEN_ID.get());
		return GEN_ID.getAndIncrement();
	}
	protected static void freeId(int id)
	{
		USED_GEN_IDS.remove((Integer) id);
		GEN_ID.set(id);
	}

	protected final List<OITCPlayer> players;
	protected ArenaVote votes;

	protected volatile boolean running = false;
	protected volatile boolean ended = false;
	protected volatile boolean votePeriod = true;

	protected Arena arena;
	protected int maxPlayers;
	protected final int matchId;

	protected final MatchManager mgr;

	public Match(MatchManager mgr)
	{
		this.mgr = mgr;
		this.matchId = generateId();
		maxPlayers = mgr.oitc.settings().getMaxPlayersPerMatch();
		players = new ArrayList<>(maxPlayers);
		votes = new ArenaVote(this);
	}

	public int getMatchId()
	{
		return matchId;
	}

	public synchronized ArenaVote votes()
	{
		return votes;
	}

	public int getFreeSlots()
	{
		return Math.max(0, mgr.oitc.settings().getMaxPlayersPerMatch() - getPlayerCount());
	}

	public enum Response { SUCCESS_JOIN, FAIL_FULL, FAIL_ENDED, FAIL_STARTED }

	public Response addPlayer(OITCPlayer p)
	{
		if(ended)
			return Response.FAIL_ENDED;
		if(running)
			return Response.FAIL_STARTED;

		synchronized (players)
		{
			if (players.size() == maxPlayers)
				return Response.FAIL_FULL;

			p.setMatchId(matchId);
			players.add(p);
		}

		return Response.SUCCESS_JOIN;
	}

	public void removePlayer(OITCPlayer p)
	{
		p.setMatchId(-1);
		p.setIsPlaying(false);

		boolean hasStarted = hasStarted();

		if (hasStarted) teleportPlayerBackFromArena(p);

		synchronized (players)
		{
			players.remove(p);

			int minPlayersForMatch = mgr.oitc.settings().getMinPlayersForMatch();
			boolean cancelMatch = players.size() < minPlayersForMatch;

			if (hasStarted)
			{
				players.forEach(_p -> {
					_p.sendMessage(ChatColor.RED + p.getDisplayName() + ChatColor.RED + " left the match.");
					if (cancelMatch)
						_p.sendMessage(ChatColor.RED + "Match is being cancelled because not enough players are in it.");
				});
			}

			if (cancelMatch)
				stop();
		}
	}

	public List<OITCPlayer> getPlayers()
	{
		return players;
	}

	public int getPlayerCount()
	{
		synchronized (players)
		{
			return players.size();
		}
	}

	public synchronized void setArena(Arena arena)
	{
		this.arena = arena;
	}

	public synchronized Arena getArena()
	{
		return arena;
	}

	public void broadcastToPlayers(String message, String... nextLines)
	{
		mgr.oitc.getServer().getScheduler().runTask(mgr.oitc, () -> {
			synchronized (players)
			{
				for (OITCPlayer player : players)
				{
					Player p = player.getPlayer();
					if (p == null || !player.isOnline()) continue;
					p.sendMessage(message);
					if (nextLines != null && nextLines.length > 0)
						for (String line : nextLines) p.sendMessage(line);
				}
			}
		});
	}

	public boolean isRunning()
	{
		return running;
	}

	public boolean hasEnded()
	{
		return ended;
	}

	public boolean hasStarted()
	{
		return running && !ended;
	}

	public boolean isInVotePeriod()
	{
		return votePeriod;
	}

	public synchronized void start()
	{
		if(running) return;
		running = true;

		BukkitScheduler scheduler = mgr.oitc.getServer().getScheduler();
		scheduler.runTaskAsynchronously(mgr.oitc, this);
	}

	@Override
	public void run()
	{
		double start, passed;

		int voteTime = 20; // seconds
		int matchTime = mgr.oitc.settings().getMatchTimeInSeconds(); // seconds

		StringBuffer sb1 = new StringBuffer();
		synchronized (players)
		{
			players.forEach(p -> {
				sb1.append(p.getDisplayName());
				sb1.append(" ");
			});
		}
		broadcastToPlayers(
				ChatColor.DARK_GREEN + "A match was found for you! Players:",
				ChatColor.AQUA + sb1.toString().trim()
		);

		List<Arena> playableArenas = mgr.oitc.arenas().getPlayableArenas();

		if(playableArenas.size() == 0)
		{
			votePeriod = false;
			stop();
			broadcastToPlayers(ChatColor.RED + "No playable arenas for this match! Cancelling...");
			return;
		}

		if(playableArenas.size() > 1)
		{
			StringBuffer sb2 = new StringBuffer();
			playableArenas.forEach(a -> {
				sb2.append(a.getName());
				sb2.append(" ");
			});

			votePeriod = true;

			broadcastToPlayers(
					ChatColor.DARK_GREEN + "Vote started! Playable arenas:",
					ChatColor.AQUA + sb2.toString().trim()
			);

			start = System.currentTimeMillis() / 1000.0;

			while (running)
			{
				passed = System.currentTimeMillis() / 1000.0 - start;
				if (passed >= voteTime)
				{
					String arenaName = getVotedArena();
					Arena arena = mgr.oitc.arenas().getArena(arenaName);
					setArena(arena);
					broadcastToPlayers(ChatColor.DARK_GREEN + "Vote ended! Arena: " + ChatColor.AQUA + arenaName);
					break;
				}

				sleep(500);
			}
		}
		else
		{
			Arena arena = playableArenas.get(0);
			setArena(arena);
			broadcastToPlayers(ChatColor.DARK_GREEN + "Arena: " + ChatColor.AQUA + arena.getName());
		}

		votePeriod = false;
		start = System.currentTimeMillis() / 1000.0;
		int countdownToMatch = 10; // seconds

		while(running)
		{
			double current = System.currentTimeMillis() / 1000.0;

			if(current - start >= 1)
			{
				broadcastToPlayers(ChatColor.DARK_GREEN + "Match starting in " + ChatColor.AQUA + countdownToMatch + "" + ChatColor.DARK_GREEN + " second" + (countdownToMatch == 1 ? "" : "s") + ".");
				countdownToMatch--;
				start = current;
			}

			if(countdownToMatch <= 0)
				break;

			sleep(200);
		}

		start = System.currentTimeMillis() / 1000.0;

		synchronized (players)
		{
			for (OITCPlayer p : players)
			{
				p.setIsPlaying(true);
				teleportPlayerToArena(p);
			}
		}

		broadcastToPlayers(ChatColor.DARK_GREEN + "Match started! Remaining time: " + ChatColor.DARK_AQUA + String.format("%.1f", (mgr.oitc.settings().getMatchTimeInSeconds() + 30) / 60.0) + " min");

		while(running)
		{
			passed = System.currentTimeMillis() / 1000.0 - start;
			if(passed >= matchTime)
			{
				sleep(500);
				break;
			}
		}

		start += matchTime;
		int countdownToEnd = 30; // seconds

		while(running)
		{
			double current = System.currentTimeMillis() / 1000.0;

			if(current - start >= 1)
			{
				countdownToEnd--;
				start = current;
			}
			else
			{
				sleep(200);
				continue;
			}

			if(countdownToEnd <= 0)
			{
				// send the last msg and break the loop
				broadcastToPlayers(ChatColor.DARK_GREEN + "Match ended!");
				break;
			}

			if(countdownToEnd <= 5)
			{
				// send msg every sec
				broadcastToPlayers(ChatColor.DARK_GREEN + "Match ending in " + ChatColor.DARK_AQUA + countdownToEnd + ChatColor.DARK_GREEN + " second" + (countdownToEnd > 1 ? "s" : "") + "!");
				continue;
			}

			if(countdownToEnd <= 30 && countdownToEnd % 10 == 0)
			{
				// send msg every 10 sec
				broadcastToPlayers(ChatColor.DARK_GREEN + "Match ending in " + ChatColor.DARK_AQUA + countdownToEnd + ChatColor.DARK_GREEN + " seconds!");
			}
		}

		if(running) // if match was stopped unexpectedly, then don't print scoreboard
		{
			Scoreboard scoreboard = new ScoreboardBuilder().forMatch(this).build();
			StringBuilder sb = new StringBuilder();

			sb.append(ChatColor.GREEN)
			  .append(ChatColor.BOLD)
			  .append("Kills ")
			  .append(ChatColor.RED)
			  .append(ChatColor.BOLD)
			  .append("Deaths ")
			  .append(ChatColor.GOLD)
			  .append(ChatColor.BOLD)
			  .append("K/D")
			  .append(ChatColor.RESET)
			  .append("\n");

			for (ScoreboardEntry e : scoreboard.getEntries())
			{
				sb.append(ChatColor.AQUA)
				  .append(e.player)
				  .append(ChatColor.DARK_GREEN)
				  .append(ChatColor.BOLD)
				  .append(" >> ")
				  .append(ChatColor.RESET)
				  .append(ChatColor.GREEN)
				  .append(e.kills)
				  .append(" ")
				  .append(ChatColor.RED)
				  .append(e.deaths)
				  .append(" ")
				  .append(ChatColor.GOLD)
				  .append(String.format("%.2f", e.getKDRatio()))
				  .append(ChatColor.RESET)
				  .append("\n");
			}

			broadcastToPlayers(sb.toString().trim());
		}

		stop();
	}

	public synchronized void stop()
	{
		if(!running) return;

		synchronized (players)
		{
			for (OITCPlayer p : players)
			{
				boolean wasPlaying = p.isPlaying();
				p.setMatchId(-1);
				p.setIsPlaying(false);
				if (wasPlaying) teleportPlayerBackFromArena(p);
			}
		}

		mgr.removeMatch(matchId);

		running = false;
		ended = true;
	}

	private synchronized String getVotedArena()
	{
		if(votes().hasAnyoneVoted())
		{
			List<String> arenas = new ArrayList<>();
			Map<String, Integer> votes = votes().getAllVotes();
			int mostVotes = 0;
			for(Map.Entry<String, Integer> e : votes.entrySet())
			{
				if(!mgr.oitc.arenas().containsArena(e.getKey()) || !mgr.oitc.arenas().getArena(e.getKey()).isPlayable())
					continue;

				if(mostVotes > e.getValue())
					continue;
				if(mostVotes < e.getValue())
				{
					arenas.clear();
					mostVotes = e.getValue();
				}

				arenas.add(e.getKey());
			}

			int randomIndex = ThreadLocalRandom.current().nextInt(arenas.size()) % arenas.size();
			return arenas.get(randomIndex);
		}

		List<Arena> arenas = mgr.oitc.arenas().getPlayableArenas();
		int randomIndex = ThreadLocalRandom.current().nextInt(arenas.size()) % arenas.size();
		return arenas.get(randomIndex).getName();
	}

	private synchronized void teleportPlayerToArena(OITCPlayer p)
	{
		mgr.oitc.getServer().getScheduler().runTask(mgr.oitc, () -> {
			p.saveState();

			ArenaPlayerState state = new ArenaPlayerState(p, arena.getRandomSpawn());
			state.saveToPlayer();
		});
	}

	private synchronized void teleportPlayerBackFromArena(OITCPlayer p)
	{
		mgr.oitc.getServer().getScheduler().runTask(mgr.oitc, p::loadState);
	}

	private void sleep(int millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch(InterruptedException e)
		{

		}
	}
}

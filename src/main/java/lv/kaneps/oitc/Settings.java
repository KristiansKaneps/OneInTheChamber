package lv.kaneps.oitc;

import java.util.List;

public class Settings
{
	protected int maxPlayersPerMatch, minPlayersForMatch, swordKillArrows, bowKillArrows;
	protected float removeArenaSpawnRadius;
	protected int matchTimeInSeconds, matchForceStartTimeInSeconds;
	protected boolean explosionOnDeath;
	protected List<String> allowedCommands;
	protected boolean playerStateSaveFile;

	protected final OITC oitc;

	public Settings(OITC oitc)
	{
		this.oitc = oitc;
	}

	public void load()
	{
		maxPlayersPerMatch = oitc.getConfig().getInt("max_players_per_match");
		minPlayersForMatch = oitc.getConfig().getInt("min_players_for_match");
		swordKillArrows = oitc.getConfig().getInt("sword_kill_arrows");
		bowKillArrows = oitc.getConfig().getInt("bow_kill_arrows");
		removeArenaSpawnRadius = (float) oitc.getConfig().getDouble("remove_spawn_radius");
		matchTimeInSeconds = oitc.getConfig().getInt("match_time");
		matchForceStartTimeInSeconds = oitc.getConfig().getInt("match_force_start_time");
		explosionOnDeath = oitc.getConfig().getBoolean("explosion_on_death");
		allowedCommands = oitc.getConfig().getStringList("allowed_commands");
		playerStateSaveFile = oitc.getConfig().getBoolean("player_state_save_file");
	}

	public int getMaxPlayersPerMatch()
	{
		return maxPlayersPerMatch;
	}

	public int getMinPlayersForMatch()
	{
		return minPlayersForMatch;
	}

	public int getSwordKillArrowReward()
	{
		return swordKillArrows;
	}

	public int getBowKillArrowReward()
	{
		return bowKillArrows;
	}

	public float getRemoveArenaSpawnRadius()
	{
		return removeArenaSpawnRadius;
	}

	public int getMatchTimeInSeconds()
	{
		return matchTimeInSeconds;
	}

	public int getMatchForceStartTimeInSeconds()
	{
		return matchForceStartTimeInSeconds;
	}

	public boolean explodeOnDeath()
	{
		return explosionOnDeath;
	}

	public List<String> getAllowedCommands()
	{
		return allowedCommands;
	}

	public boolean savePlayerStateToFile()
	{
		return playerStateSaveFile;
	}
}

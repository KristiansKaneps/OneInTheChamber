package lv.kaneps.oitc.scoreboard;

public class ScoreboardEntry
{
	public final int kills, deaths;

	public final String player;

	public ScoreboardEntry(String player, int kills, int deaths)
	{
		this.player = player;
		this.kills = kills;
		this.deaths = deaths;
	}

	public float getKDRatio()
	{
		return (float) kills / (float) deaths;
	}
}

package lv.kaneps.oitc.scoreboard;

import lv.kaneps.oitc.match.Match;
import lv.kaneps.oitc.player.OITCPlayer;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardBuilder
{
	protected Match match;

	public ScoreboardBuilder() { }

	public ScoreboardBuilder forMatch(Match match)
	{
		this.match = match;
		return this;
	}

	public Scoreboard build()
	{
		int size = match.getPlayerCount();

		Scoreboard sb = new Scoreboard(size);
		List<OITCPlayer> list = new ArrayList<>(match.getPlayers());
		list.forEach(p -> sb.add(new ScoreboardEntry(p.getDisplayName(), p.getKills(), p.getDeaths())));

		return sb;
	}
}

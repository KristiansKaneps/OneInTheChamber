package lv.kaneps.oitc.scoreboard;

public class Scoreboard
{
	protected final ScoreboardEntry[] entries;
	protected int validEntries = 0;

	protected final int size;

	public Scoreboard(int size)
	{
		this.size = size;
		entries = new ScoreboardEntry[size];
	}

	public boolean isFull()
	{
		return validEntries == size;
	}

	public ScoreboardEntry[] getEntries()
	{
		return entries;
	}

	public ScoreboardEntry getEntry(int index)
	{
		return entries[index];
	}

	public ScoreboardEntry getEntry(String player)
	{
		for(ScoreboardEntry e : entries)
			if(player.equalsIgnoreCase(e.player))
				return e;
		return null;
	}

	/**
	 * @param entry - entry to add
	 * @return -1 if couldn't add (scoreboard full) or index in entries array
	 */
	public int add(ScoreboardEntry entry)
	{
		for(int i = 0; i < size; i++)
		{
			if(entries[i] != null) continue;
			entries[i] = entry;
			validEntries++;
			return i;
		}
		return -1;
	}

	public void remove(String player)
	{
		for(int i = 0; i < size; i++)
		{
			ScoreboardEntry e = entries[i];
			if(!player.equalsIgnoreCase(e.player)) continue;
			entries[i] = null;
			validEntries--;
		}
	}

	public void clear()
	{
		for(int i = 0; i < size; i++)
			entries[i] = null;
		validEntries = 0;
	}
}

package lv.kaneps.oitc.match;

import lv.kaneps.oitc.player.OITCPlayer;

import java.util.LinkedList;

public class PlayerQueue
{
	protected final LinkedList<OITCPlayer> queue;

	protected final Listener listener;

	public PlayerQueue(Listener listener)
	{
		this.listener = listener;
		queue = new LinkedList<>();
	}

	public PlayerQueue()
	{
		this(null);
	}

	public int size()
	{
		return queue.size();
	}

	public void enqueue(OITCPlayer p)
	{
		queue.add(p);
		if(listener != null) listener.onPlayerEnqueue(p);
	}

	public OITCPlayer dequeue()
	{
		if(queue.size() == 0) return null;
		OITCPlayer p = queue.remove();
		if(listener != null) listener.onPlayerDequeue(p);
		return p;
	}

	public void remove(OITCPlayer p)
	{
		boolean removed = queue.remove(p);
		if(removed && listener != null) listener.onPlayerRemoved(p);
	}

	public boolean contains(OITCPlayer p)
	{
		return queue.contains(p);
	}

	public void clear()
	{
		queue.clear();
	}

	public interface Listener
	{
		void onPlayerEnqueue(OITCPlayer player);
		void onPlayerDequeue(OITCPlayer player);
		void onPlayerRemoved(OITCPlayer player);
	}
}

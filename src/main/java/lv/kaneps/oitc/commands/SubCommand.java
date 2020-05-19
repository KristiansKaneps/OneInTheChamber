package lv.kaneps.oitc.commands;

import lv.kaneps.oitc.player.OITCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class SubCommand
{
	protected final BaseCommand base;
	protected final OITCPlayer p;
	protected final String permission;
	protected final boolean mustNotBeInAMatch;

	public SubCommand(BaseCommand base, Player p)
	{
		this.base = base;
		this.p = OITCPlayer.getPlayer(p);
		this.permission = null;
		this.mustNotBeInAMatch = false;
	}

	public SubCommand(BaseCommand base, Player p, String permission)
	{
		this.base = base;
		this.p = OITCPlayer.getPlayer(p);
		this.permission = permission;
		this.mustNotBeInAMatch = false;
	}

	public SubCommand(BaseCommand base, Player p, String permission, boolean mustNotBeInAMatch)
	{
		this.base = base;
		this.p = OITCPlayer.getPlayer(p);
		this.permission = permission;
		this.mustNotBeInAMatch = mustNotBeInAMatch;
	}

	public BaseCommand getBaseCommand()
	{
		return base;
	}

	public OITCPlayer getPlayer()
	{
		return p;
	}

	public String getPermission()
	{
		return permission;
	}

	public boolean hasPermission()
	{
		return permission == null || p.getPlayer().hasPermission(permission);
	}

	public void sendNoPermissionsMessage()
	{
		BaseCommand.noPerms(p.getPlayer());
	}

	public final boolean execute()
	{
		if(!p.isOnline())
			return true;

		if(mustNotBeInAMatch && p.isInMatch())
		{
			p.sendMessage(ChatColor.RED + "You can't use this command while you are playing OITC.");
			return true;
		}

		if(hasPermission())
			return onCommand();
		else
			sendNoPermissionsMessage();
		return true;
	}

	public abstract boolean onCommand();
}

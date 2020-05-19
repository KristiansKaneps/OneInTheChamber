package lv.kaneps.oitc.commands;

import lv.kaneps.oitc.OITC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BaseCommand implements CommandExecutor
{
	protected final Map<String, String> selectedArenas = new HashMap<>();

	protected final OITC oitc;

	public BaseCommand(OITC oitc)
	{
		this.oitc = oitc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage("You must be a player to use this command.");
			return true;
		}

		Player p = (Player) sender;

		if(!p.hasPermission("oitc.command"))
		{
			noPerms(p);
			return true;
		}

		/*
		 * /oitc join
		 * /oitc leave
		 * /oitc vote <name>

		 * /oitc (help|usage)
		 * /oitc arena list or /oitc (arenas|list)
		 * /oitc arena create <name>
		 * /oitc arena (remove|delete) <name>
		 * /oitc arena displayname <name> <displayname>
		 * /oitc arena select <name>
		 * /oitc arena addspawn <name>
		 * /oitc arena (removespawn|delspawn) <name>
		 * /oitc arena validate <name>
		 * /oitc arena invalidate <name>
		 * /oitc addspawn
		 * /oitc (removespawn|delspawn)
		 */

		if(args.length == 1)
		{
			if(args[0].equalsIgnoreCase("join"))
				return new JoinCommand(this, p).execute();
			if(args[0].equalsIgnoreCase("leave"))
				return new LeaveCommand(this, p).execute();
			if(args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("arenas"))
				return new ArenaListCommand(this, p).execute();
			if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("usage"))
				return new HelpCommand(this, p).execute();

			if(selectedArenas.containsKey(p.getName()))
			{
				return findAndExecArenaCommand(p, args, selectedArenas.get(p.getName()));
			}

			return false;
		}

		if(args.length == 2)
		{
			if(args[0].equalsIgnoreCase("vote"))
				return new VoteCommand(this, p, args[1]).execute();
			if(args[0].equalsIgnoreCase("displayname"))
				return new ArenaDisplaynameCommand(this, p, selectedArenas.get(p.getName()), args[1]).execute();

			if(args[0].equalsIgnoreCase("arena"))
			{
				if(args[1].equalsIgnoreCase("list"))
					return new ArenaListCommand(this, p).execute();
			}

			return false;
		}

		if(args.length == 3)
		{
			if(args[0].equalsIgnoreCase("arena"))
			{
				return findAndExecArenaCommand(p, args, null);
			}

			return false;
		}

		if(args.length == 4)
		{
			if(args[0].equalsIgnoreCase("arena"))
			{
				if(args[1].equalsIgnoreCase("displayname"))
					return new ArenaDisplaynameCommand(this, p, args[2], args[3]).execute();
			}

			return false;
		}

		return false;
	}

	private boolean findAndExecArenaCommand(Player p, String[] args, String arenaName)
	{
		String cmdArg = arenaName == null ? args[1] : args[0];
		arenaName = arenaName == null ? args[2] : arenaName;

		if(cmdArg.equalsIgnoreCase("create"))
			return new ArenaCreateCommand(this, p, arenaName).execute();
		else if(cmdArg.equalsIgnoreCase("remove") || cmdArg.equalsIgnoreCase("delete"))
			return new ArenaRemoveCommand(this, p, arenaName).execute();
		else if(cmdArg.equalsIgnoreCase("select"))
			return new ArenaSelectCommand(this, p, arenaName).execute();
		else if(cmdArg.equalsIgnoreCase("addspawn"))
			return new AddSpawnCommand(this, p, arenaName).execute();
		else if(cmdArg.equalsIgnoreCase("removespawn") || cmdArg.equalsIgnoreCase("delspawn"))
			return new RemoveSpawnCommand(this, p, arenaName).execute();
		else if(cmdArg.equalsIgnoreCase("validate") || args[1].equalsIgnoreCase("invalidate"))
			return new ArenaValidationCommand(this, p, arenaName, cmdArg.equalsIgnoreCase("validate")).execute();
		return false;
	}

	protected static void arenaNotFound(Player p, String arenaName)
	{
		error(p, "Arena with name '" + ChatColor.AQUA + arenaName + ChatColor.RED + "' not found.");
	}

	protected static void noPerms(Player p)
	{
		error(p, "You don't have enough permissions to use this command.");
	}

	protected static void error(Player p, String msg)
	{
		p.sendMessage(ChatColor.RED + msg);
	}

	protected static void success(Player p, String msg)
	{
		p.sendMessage(ChatColor.DARK_GREEN + msg);
	}
}

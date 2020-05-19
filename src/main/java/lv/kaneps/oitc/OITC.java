package lv.kaneps.oitc;

import lv.kaneps.oitc.arena.Arena;
import lv.kaneps.oitc.arena.ArenaStorage;
import lv.kaneps.oitc.commands.BaseCommand;
import lv.kaneps.oitc.events.ArenaEvents;
import lv.kaneps.oitc.events.CommandEvents;
import lv.kaneps.oitc.match.MatchManager;
import lv.kaneps.oitc.player.OITCPlayer;
import lv.kaneps.oitc.player.PlayerStateSave;
import org.bukkit.Server;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;

public class OITC extends JavaPlugin
{
	static
	{
		ConfigurationSerialization.registerClass(Arena.class, "Arena");
	}

	public static OITCPlayer getPlayer(Player player)
	{
		return OITCPlayer.getPlayer(player);
	}

	public static OITC instance;

	public static final String NAME = "OITC";
	public static final String PREFIX = "[" + NAME + "] ";

	public static Logger log;

	protected MatchManager matchManager;

	protected Settings settings;
	protected ArenaStorage arenas;

	@Override
	public void onEnable()
	{
		instance = this;

		Server server = getServer();
		PluginManager pm = server.getPluginManager();
		log = server.getLogger();

		settings = new Settings(this);
		arenas = new ArenaStorage(this, "arenas.yml");
		matchManager = new MatchManager(this);

		info("Loading config & arenas.");
		loadConfig();
		settings.load();
		arenas.load();

		info("Registering event handlers.");
		pm.registerEvents(new PlayerStateSave.PlayerStateSaveEvents(), this);
		pm.registerEvents(new OITCPlayer.OITCPlayerEvents(), this);
		pm.registerEvents(new CommandEvents(this), this);
		pm.registerEvents(new ArenaEvents(this), this);

		info("Registering commands.");
		Objects.requireNonNull(getCommand("oitc")).setExecutor(new BaseCommand(this));
	}

	@Override
	public void onDisable()
	{
		matchManager.stopAll();
	}

	public Settings settings()
	{
		return settings;
	}

	public ArenaStorage arenas()
	{
		return arenas;
	}

	public MatchManager matchManager()
	{
		return matchManager;
	}

	private void loadConfig()
	{
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	public static void info(Object obj)
	{
		log.info(PREFIX + obj.toString());
	}

	public static void warn(Object obj)
	{
		log.warning(PREFIX + obj.toString());
	}
}
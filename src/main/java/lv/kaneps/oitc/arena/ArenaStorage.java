package lv.kaneps.oitc.arena;

import lv.kaneps.oitc.OITC;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ArenaStorage
{
	protected final Map<String, Arena> arenas = new HashMap<>();

	protected final File file;
	protected YamlConfiguration config;

	protected final OITC oitc;

	public ArenaStorage(OITC oitc, String filename)
	{
		this.oitc = oitc;

		file = new File(oitc.getDataFolder() + "/" + filename);
		config = YamlConfiguration.loadConfiguration(file);
	}

	protected void saveArena(Arena arena)
	{
		synchronized (arenas)
		{
			if(!arenas.containsKey(arena.getName())) arenas.put(arena.getName(), arena);
			config.set("arenas." + arena.getName(), arena);
		}
	}

	public boolean containsArena(String name)
	{
		synchronized (arenas)
		{
			return arenas.containsKey(name);
		}
	}

	public void addArena(Arena arena)
	{
		saveArena(arena);
	}

	public void removeArena(String name)
	{
		synchronized (arenas)
		{
			arenas.remove(name);
			config.set("arenas." + name, null);
		}

		save();
	}

	public Arena getArena(String name)
	{
		synchronized (arenas)
		{
			return arenas.get(name);
		}
	}

	public List<Arena> getArenas()
	{
		synchronized (arenas)
		{
			return new ArrayList<>(arenas.values());
		}
	}

	public List<Arena> getPlayableArenas()
	{
		synchronized (arenas)
		{
			List<Arena> arenas = new ArrayList<>();
			for(Map.Entry<String, Arena> e : this.arenas.entrySet())
			{
				Arena a = e.getValue();
				if(a.isPlayable())
					arenas.add(a);
			}
			return arenas;
		}
	}

	public int getArenaCount()
	{
		synchronized (arenas)
		{
			return arenas.size();
		}
	}

	public int getPlayableArenaCount()
	{
		synchronized (arenas)
		{
			int playableArenaCount = 0;
			for(Map.Entry<String, Arena> e : this.arenas.entrySet())
				if(e.getValue().isPlayable()) playableArenaCount++;
			return playableArenaCount;
		}
	}

	public void save()
	{
		for (Map.Entry<String, Arena> entry : arenas.entrySet())
		{
			saveArena(entry.getValue());
		}

		try
		{
			config.save(file);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public void load()
	{
		if(!config.contains("arenas")) return;
		ConfigurationSection section = config.getConfigurationSection("arenas");
		if(section == null) return;
		synchronized (arenas)
		{
			for (String key : section.getKeys(false))
			{
				Arena arena = section.getSerializable(key, Arena.class);
				if(arena == null) continue;
				arenas.put(arena.getName(), arena);
			}
		}
	}
}

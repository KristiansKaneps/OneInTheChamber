package lv.kaneps.oitc.arena;

import lv.kaneps.oitc.math.Sphere;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@SerializableAs("Arena")
public class Arena implements Cloneable, ConfigurationSerializable
{
	protected final List<Location> spawns = new ArrayList<>();

	protected String displayName;
	protected boolean valid;

	protected final String name;

	public Arena(String name)
	{
		this.name = name;
		this.displayName = name;
		valid = false;
	}

	public String getName()
	{
		return name;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public List<Location> getSpawnLocations()
	{
		return spawns;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public void addSpawn(Location loc)
	{
		synchronized (spawns)
		{
			spawns.add(loc);
		}
	}

	public Location removeSpawnInRadius(Location loc, float radius)
	{
		synchronized (spawns)
		{
			Iterator<Location> iterator = spawns.iterator();
			while(iterator.hasNext())
			{
				Location iterLoc = iterator.next();

				Sphere sphere = new Sphere(loc.getX(), loc.getY(), loc.getZ(), radius);

				if(sphere.isPointInside(iterLoc.getX(), iterLoc.getY(), iterLoc.getZ()))
				{
					iterator.remove();
					return iterLoc;
				}
			}
		}
		return null;
	}

	public boolean isPlayable()
	{
		synchronized (spawns)
		{
			return isValid() && spawns.size() > 0;
		}
	}

	public boolean isValid()
	{
		return valid;
	}

	public void validate()
	{
		valid = true;
	}

	public void invalidate()
	{
		valid = false;
	}

	public Location getRandomSpawn()
	{
		synchronized (spawns)
		{
			// todo: don't spawn more than 1 player at the same location
			int randomIndex = ThreadLocalRandom.current().nextInt(spawns.size()) % spawns.size();
			Location loc = spawns.get(randomIndex);
			if(loc == null)
				return spawns.get(0);
			return loc;
		}
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("name", name);
		map.put("displayname", displayName);
		map.put("valid", valid);
		synchronized (spawns)
		{
			map.put("spawns", spawns);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public static Arena deserialize(Map<String, Object> map)
	{
		String name = (String) map.get("name");

		Arena arena = new Arena(name);

		if(map.containsKey("displayname"))
			arena.displayName = (String) map.get("displayname");

		if(map.containsKey("valid"))
			arena.valid = (boolean) map.get("valid");

		if(map.containsKey("spawns"))
		{
			List<Object> _spawns = (List<Object>) map.get("spawns");
			for(Object obj : _spawns)
			{
				arena.addSpawn((Location) obj);
			}
		}

		return arena;
	}
}

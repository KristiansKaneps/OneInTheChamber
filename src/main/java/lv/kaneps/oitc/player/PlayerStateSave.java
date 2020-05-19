package lv.kaneps.oitc.player;

import lv.kaneps.oitc.OITC;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerStateSave
{
	private static final Object syncLock = new Object();
	private static final List<UUID> deleteOnWorldSave = new ArrayList<>();
	private static final List<UUID> returnOnJoin = new ArrayList<>();
	private static final Map<UUID, PlayerStateSave> saves = new HashMap<>();

	public static void markSaveForDelete(OITCPlayer player)
	{
		synchronized (syncLock)
		{
			if (!deleteOnWorldSave.contains(player.getUniqueId()))
				deleteOnWorldSave.add(player.getUniqueId());
		}
	}

	public static void unmarkSaveForDelete(OITCPlayer player)
	{
		synchronized (syncLock)
		{
			deleteOnWorldSave.remove(player.getUniqueId());
		}
	}

	public static void returnStateOnJoin(OITCPlayer player)
	{
		synchronized (syncLock)
		{
			returnOnJoin.add(player.getUniqueId());
		}
	}

	public static void deleteMarkedSaves()
	{
		synchronized (syncLock)
		{
			Iterator<UUID> iterator = deleteOnWorldSave.iterator();
			UUID uuid;
			while(iterator.hasNext())
			{
				uuid = iterator.next();
				if(!returnOnJoin.contains(uuid))
				{
					if (saves.containsKey(uuid))
						saves.get(uuid).deleteSave();
				}
				iterator.remove();
			}
		}
	}

	public static class PlayerStateSaveEvents implements Listener
	{
		@EventHandler(priority = EventPriority.LOW)
		public void onWorldSave(WorldSaveEvent event)
		{
			deleteMarkedSaves();
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		public void onPlayerJoin(PlayerJoinEvent event)
		{
			synchronized (syncLock)
			{
				if(!returnOnJoin.contains(event.getPlayer().getUniqueId()))
					return;
			}

			OITCPlayer p = OITCPlayer.getPlayer(event.getPlayer());
			PlayerStateSave save = new PlayerStateSave(p);
			if(save.load())
				save.saveToPlayer();

			synchronized (syncLock)
			{
				returnOnJoin.remove(p.getUniqueId());
			}

			markSaveForDelete(p);
		}
	}

	protected static String getRelativeFileName(OITCPlayer player)
	{
		return "/states/" + player.getUniqueId() + ".yml";
	}

	protected String displayName;

	protected Location location;
	protected float fallDistance;
	protected boolean flying;
	protected float walkSpeed, flySpeed;
	protected Vector velocity;
	protected boolean gliding, glowing;

	protected boolean invulnerable;
	protected double health;
	protected int remainingAir;
	protected float saturation;
	protected int foodLevel, expLevel;
	protected float exp, exhaustion;
	protected int fireTicks, noDamageTicks;
	protected GameMode gameMode;

	protected ItemStack helmet, chestplate, leggings, boots, offhand;
	protected ItemStack[] extra, storageContents;

	protected int heldItemSlot;

	protected final OITCPlayer player;

	public PlayerStateSave(OITCPlayer player)
	{
		this.player = player;
	}

	public void saveToPlayer()
	{
		player.setDisplayName(displayName);

		player.setFallDistance(fallDistance);
		if(OITC.instance.getServer().getAllowFlight()) player.setFlying(flying);
		player.setWalkSpeed(walkSpeed);
		player.setFlySpeed(flySpeed);
		player.setVelocity(velocity);
		player.setGliding(gliding);
		player.setGlowing(glowing);

		player.setInvulnerable(invulnerable);
		player.setHealth(health);
		player.setRemainingAir(remainingAir);
		player.setFoodLevel(foodLevel);
		player.setSaturation(saturation);
		player.setExpLevel(expLevel);
		player.setExp(exp);
		player.setExhaustion(exhaustion);
		player.setFireTicks(fireTicks);
		player.setNoDamageTicks(noDamageTicks);
		player.setGameMode(gameMode);

		PlayerInventory inv = player.getInventory();
		inv.setHelmet(helmet);
		inv.setChestplate(chestplate);
		inv.setLeggings(leggings);
		inv.setBoots(boots);
		inv.setItemInOffHand(offhand);
		inv.setExtraContents(extra);
		inv.setStorageContents(storageContents);

		inv.setHeldItemSlot(heldItemSlot);

		player.teleport(location);
	}

	public void setFromOtherSave(PlayerStateSave save)
	{
		displayName = save.displayName;

		location = save.location;
		fallDistance = save.fallDistance;
		flying = save.flying;
		walkSpeed = save.walkSpeed;
		flySpeed = save.flySpeed;
		velocity = save.velocity;
		gliding = save.gliding;
		glowing = save.glowing;

		invulnerable = save.invulnerable;
		health = save.health;
		remainingAir = save.remainingAir;
		saturation = save.saturation;
		foodLevel = save.foodLevel;
		expLevel = save.expLevel;
		exp = save.exp;
		exhaustion = save.exhaustion;
		fireTicks = save.fireTicks;
		noDamageTicks = save.noDamageTicks;
		gameMode = save.gameMode;

		helmet = save.helmet;
		chestplate = save.chestplate;
		leggings = save.leggings;
		boots = save.boots;
		offhand = save.offhand;
		extra = save.extra;
		storageContents = save.storageContents;

		heldItemSlot = save.heldItemSlot;
	}

	public void loadFromPlayer()
	{
		PlayerInventory inv = player.getInventory();

		displayName = player.getDisplayName();

		location = player.getLocation();
		fallDistance = player.getFallDistance();
		flying = player.isFlying();
		walkSpeed = player.getWalkSpeed();
		flySpeed = player.getFlySpeed();
		velocity = player.getVelocity();
		gliding = player.isGliding();
		glowing = player.isGlowing();

		invulnerable = player.isInvulnerable();
		health = player.getHealth();
		remainingAir = player.getRemainingAir();
		saturation = player.getSaturation();
		foodLevel = player.getFoodLevel();
		expLevel = player.getExpLevel();
		exp = player.getExp();
		exhaustion = player.getExhaustion();
		fireTicks = player.getFireTicks();
		noDamageTicks = player.getNoDamageTicks();
		gameMode = player.getGameMode();

		helmet = inv.getHelmet();
		chestplate = inv.getChestplate();
		leggings = inv.getLeggings();
		boots = inv.getBoots();
		offhand = inv.getItemInOffHand();
		extra = inv.getExtraContents();
		storageContents = inv.getStorageContents();

		heldItemSlot = inv.getHeldItemSlot();
	}

	/**
	 * Saves to file (if not disabled in config) and HashMap.
	 */
	public void save()
	{
		saves.put(player.getUniqueId(), this);

		if(!OITC.instance.settings().savePlayerStateToFile())
			return;

		File file = new File(OITC.instance.getDataFolder() + getRelativeFileName(player));
		if(!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		if(file.exists()) file.delete();

		FileConfiguration save = YamlConfiguration.loadConfiguration(file);

		save.set("displayName", displayName);

		save.set("location", location);
		save.set("fallDistance", (double) fallDistance);
		save.set("flying", flying);
		save.set("walkSpeed", (double) walkSpeed);
		save.set("flySpeed", (double) flySpeed);
		save.set("velocity", velocity);
		save.set("gliding", gliding);
		save.set("glowing", glowing);

		save.set("invulnerable", invulnerable);
		save.set("health", health);
		save.set("remainingAir", remainingAir);
		save.set("saturation", (double) saturation);
		save.set("foodLevel", foodLevel);
		save.set("expLevel", expLevel);
		save.set("exp", (double) exp);
		save.set("exhaustion", (double) exhaustion);
		save.set("fireTicks", fireTicks);
		save.set("noDamageTicks", noDamageTicks);
		save.set("gameMode", gameMode.ordinal());

		save.set("helmet", helmet);
		save.set("chestplate", chestplate);
		save.set("leggings", leggings);
		save.set("boots", boots);
		save.set("offhand", offhand);
		save.set("extra", Arrays.asList(extra));
		save.set("storageContents", Arrays.asList(storageContents));

		save.set("heldItemSlot", heldItemSlot);

		try
		{
			save.save(file);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Loads from file (if not disabled in config) or HashMap.
	 * @return false if failed to load, true otherwise
	 */
	public boolean load()
	{
		String fileLoc = OITC.instance.getDataFolder() + getRelativeFileName(player);

		if(saves.containsKey(player.getUniqueId()))
		{
			PlayerStateSave save = saves.get(player.getUniqueId());
			setFromOtherSave(save);
			if(OITC.instance.settings().savePlayerStateToFile())
			{
				File file = new File(fileLoc);
				if (file.exists()) file.delete();
			}
			return true;
		}

		if(!OITC.instance.settings().savePlayerStateToFile())
			return false;

		File file = new File(fileLoc);
		if (!file.exists()) return false;

		FileConfiguration save = YamlConfiguration.loadConfiguration(file);

		displayName = save.getString("displayName");

		location = save.getLocation("location");
		fallDistance = (float) save.getDouble("fallDistance");
		flying = save.getBoolean("flying");
		walkSpeed = (float) save.getDouble("walkSpeed");
		flySpeed = (float) save.getDouble("flySpeed");
		velocity = save.getVector("velocity");
		gliding = save.getBoolean("gliding");
		glowing = save.getBoolean("glowing");

		invulnerable = save.getBoolean("invulnerable");
		health = save.getDouble("health");
		remainingAir = save.getInt("remainingAir");
		saturation = (float) save.getDouble("saturation");
		foodLevel = save.getInt("foodLevel");
		expLevel = save.getInt("expLevel");
		exp = (float) save.getDouble("exp");
		exhaustion = (float) save.getDouble("exhaustion");
		fireTicks = save.getInt("fireTicks");
		noDamageTicks = save.getInt("noDamageTicks");
		gameMode = GameMode.values()[save.getInt("gameMode")];

		helmet = save.getItemStack("helmet");
		chestplate = save.getItemStack("chestplate");
		leggings = save.getItemStack("leggings");
		boots = save.getItemStack("boots");
		offhand = save.getItemStack("offhand");
		List<?> extraList = save.getList("extra"); extra = (extraList == null) ? new ItemStack[0] : extraList.toArray(new ItemStack[0]);
		List<?> storageContentsList = save.getList("storageContents"); storageContents = (storageContentsList == null) ? new ItemStack[0] : storageContentsList.toArray(new ItemStack[0]);

		heldItemSlot = save.getInt("heldItemSlot");

		return true;
	}

	/**
	 * Deletes this state save permanently.
	 */
	public void deleteSave()
	{
		saves.remove(player.getUniqueId());

		if(OITC.instance.settings().savePlayerStateToFile())
		{
			File file = new File(OITC.instance.getDataFolder() + getRelativeFileName(player));
			if(file.exists()) file.delete();
		}
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public Location getLocation()
	{
		return location;
	}

	public float getFallDistance()
	{
		return fallDistance;
	}

	public boolean isFlying()
	{
		return flying;
	}

	public float getWalkSpeed()
	{
		return walkSpeed;
	}

	public float getFlySpeed()
	{
		return flySpeed;
	}

	public Vector getVelocity()
	{
		return velocity;
	}

	public boolean isGliding()
	{
		return gliding;
	}

	public boolean isGlowing()
	{
		return glowing;
	}

	public boolean isInvulnerable()
	{
		return invulnerable;
	}

	public double getHealth()
	{
		return health;
	}

	public int getRemainingAir()
	{
		return remainingAir;
	}

	public float getSaturation()
	{
		return saturation;
	}

	public int getFoodLevel()
	{
		return foodLevel;
	}

	public int getExpLevel()
	{
		return expLevel;
	}

	public float getExp()
	{
		return exp;
	}

	public float getExhaustion()
	{
		return exhaustion;
	}

	public int getFireTicks()
	{
		return fireTicks;
	}

	public int getNoDamageTicks()
	{
		return noDamageTicks;
	}

	public GameMode getGameMode()
	{
		return gameMode;
	}

	public ItemStack getHelmet()
	{
		return helmet;
	}

	public ItemStack getChestplate()
	{
		return chestplate;
	}

	public ItemStack getLeggings()
	{
		return leggings;
	}

	public ItemStack getBoots()
	{
		return boots;
	}

	public ItemStack getOffhand()
	{
		return offhand;
	}

	public ItemStack[] getExtra()
	{
		return extra;
	}

	public ItemStack[] getStorageContents()
	{
		return storageContents;
	}
}

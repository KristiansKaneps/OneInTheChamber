package lv.kaneps.oitc.player;

import lv.kaneps.oitc.OITC;
import lv.kaneps.oitc.match.Match;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OITCPlayer
{
	private static final Map<UUID, OITCPlayer> oitcPlayers = new HashMap<>();

	public static OITCPlayer getPlayer(Player player)
	{
		UUID uuid = player.getUniqueId();
		OITCPlayer p;

		if(!oitcPlayers.containsKey(uuid))
		{
			p = new OITCPlayer(player);
			oitcPlayers.put(uuid, p);
		}
		else
		{
			p = oitcPlayers.get(player.getUniqueId());
		}

		return p;
	}

	public static class OITCPlayerEvents implements Listener
	{
		@EventHandler(priority = EventPriority.HIGH)
		public void onPlayerQuit(PlayerQuitEvent event)
		{
			handleDisconnection(event.getPlayer());
		}

		@EventHandler(priority = EventPriority.HIGH)
		public void onPlayerQuit(PlayerKickEvent event)
		{
			handleDisconnection(event.getPlayer());
		}

		private void handleDisconnection(Player player)
		{
			UUID uuid = player.getUniqueId();

			if(!oitcPlayers.containsKey(uuid)) return;

			OITCPlayer p = oitcPlayers.get(uuid);
			p.disconnected = true;

			if(p.isInMatch() || p.isPlaying())
			{
				Match match = OITC.instance.matchManager().getMatch(p.getMatchId());

				if(p.isPlaying())
					PlayerStateSave.returnStateOnJoin(p);

				match.removePlayer(p);
			}

			oitcPlayers.remove(uuid);
		}
	}

	protected boolean disconnected = false;

	protected int matchId = -1;
	protected boolean isPlaying = false;

	protected int kills = 0;
	protected int deaths = 0;

	protected final Player player;

	public OITCPlayer(Player player)
	{
		this.player = player;
		oitcPlayers.put(player.getUniqueId(), this);
	}

	public void saveState()
	{
		PlayerStateSave save = new PlayerStateSave(this);
		PlayerStateSave.unmarkSaveForDelete(this);
		save.loadFromPlayer();
		save.save();
	}

	public void loadState()
	{
		PlayerStateSave save = new PlayerStateSave(this);
		if(!save.load()) OITC.warn("Couldn't load player's save (username=" + this.getName() + "; uuid=" + this.getUniqueId() + ")!");
		save.saveToPlayer();
		PlayerStateSave.markSaveForDelete(this);
	}

	public Player getPlayer()
	{
		return player;
	}

	public AttributeInstance getAttribute(Attribute attribute)
	{
		return player.getAttribute(attribute);
	}

	public boolean isOnline()
	{
		return !disconnected;
	}

	public boolean isInMatch()
	{
		return matchId != -1;
	}

	public boolean isPlaying()
	{
		return isPlaying;
	}

	public void setMatchId(int matchId)
	{
		this.matchId = matchId;
	}

	public void setIsPlaying(boolean isPlaying)
	{
		this.isPlaying = isPlaying;
	}

	public int getMatchId()
	{
		return matchId;
	}

	public Match getMatch()
	{
		return OITC.instance.matchManager().getMatch(matchId);
	}

	public int incrementKills()
	{
		return ++kills;
	}

	public int incrementDeaths()
	{
		return ++deaths;
	}

	public int getKills()
	{
		return kills;
	}

	public int getDeaths()
	{
		return deaths;
	}

	public String getName()
	{
		return player.getName();
	}

	public UUID getUniqueId()
	{
		return player.getUniqueId();
	}

	public PlayerInventory getInventory()
	{
		return player.getInventory();
	}

	public void teleport(Location location)
	{
		player.teleport(location);
	}

	public void setDisplayName(String displayName)
	{
		player.setDisplayName(displayName);
	}

	public String getDisplayName()
	{
		return player.getDisplayName();
	}

	public Location getLocation()
	{
		return player.getLocation();
	}

	public void setFallDistance(float fallDistance)
	{
		player.setFallDistance(fallDistance);
	}

	public float getFallDistance()
	{
		return player.getFallDistance();
	}

	public void setFlying(boolean flying)
	{
		player.setFlying(flying);
	}

	public boolean isFlying()
	{
		return player.isFlying();
	}

	public void setWalkSpeed(float walkSpeed)
	{
		player.setWalkSpeed(walkSpeed);
	}

	public float getWalkSpeed()
	{
		return player.getWalkSpeed();
	}

	public void setFlySpeed(float flySpeed)
	{
		player.setFlySpeed(flySpeed);
	}

	public float getFlySpeed()
	{
		return player.getFlySpeed();
	}

	public void setVelocity(Vector velocity)
	{
		player.setVelocity(velocity);
	}

	public Vector getVelocity()
	{
		return player.getVelocity();
	}

	public void setGliding(boolean gliding)
	{
		player.setGliding(gliding);
	}

	public boolean isGliding()
	{
		return player.isGliding();
	}

	public void setGlowing(boolean glowing)
	{
		player.setGlowing(glowing);
	}

	public boolean isGlowing()
	{
		return player.isGlowing();
	}

	public void setInvulnerable(boolean invulnerable)
	{
		player.setInvulnerable(invulnerable);
	}

	public boolean isInvulnerable()
	{
		return player.isInvulnerable();
	}

	public void setHealth(double health)
	{
		player.setHealth(health);
	}

	public double getHealth()
	{
		return player.getHealth();
	}

	public void setRemainingAir(int remainingAirTicks)
	{
		player.setRemainingAir(remainingAirTicks);
	}

	public int getRemainingAir()
	{
		return player.getRemainingAir();
	}

	public void setSaturation(float saturation)
	{
		player.setSaturation(saturation);
	}

	public float getSaturation()
	{
		return player.getSaturation();
	}

	public void setFoodLevel(int foodLevel)
	{
		player.setFoodLevel(foodLevel);
	}

	public int getFoodLevel()
	{
		return player.getFoodLevel();
	}

	public void setExhaustion(float exhaustion)
	{
		player.setExhaustion(exhaustion);
	}

	public float getExhaustion()
	{
		return player.getExhaustion();
	}

	public void setFireTicks(int fireTicks)
	{
		player.setFireTicks(fireTicks);
	}

	public int getFireTicks()
	{
		return player.getFireTicks();
	}

	public void setNoDamageTicks(int noDamageTicks)
	{
		player.setNoDamageTicks(noDamageTicks);
	}

	public int getNoDamageTicks()
	{
		return player.getNoDamageTicks();
	}

	public void setGameMode(GameMode gameMode)
	{
		player.setGameMode(gameMode);
	}

	public GameMode getGameMode()
	{
		return player.getGameMode();
	}

	public void setExpLevel(int expLevel)
	{
		player.setLevel(expLevel);
	}

	public int getExpLevel()
	{
		return player.getLevel();
	}

	public void setExp(float exp)
	{
		player.setExp(exp);
	}

	public float getExp()
	{
		return player.getExp();
	}

	public boolean hasPermission(String permission)
	{
		return player.hasPermission(permission);
	}

	public void sendMessage(String message)
	{
		player.sendMessage(message);
	}
}

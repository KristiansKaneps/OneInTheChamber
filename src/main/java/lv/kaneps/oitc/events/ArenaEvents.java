package lv.kaneps.oitc.events;

import lv.kaneps.oitc.OITC;
import lv.kaneps.oitc.player.ArenaPlayerState;
import lv.kaneps.oitc.player.OITCPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.event.EventPriority.HIGH;

public class ArenaEvents implements Listener
{
	protected final OITC oitc;

	public ArenaEvents(OITC oitc)
	{
		this.oitc = oitc;
	}

	@EventHandler(priority = HIGH)
	public void onItemPickup(EntityPickupItemEvent event)
	{
		if(event.getEntityType() != EntityType.PLAYER) return;
		OITCPlayer p = OITCPlayer.getPlayer((Player) event.getEntity());
		if(!p.isPlaying()) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = HIGH)
	public void onItemDrop(PlayerDropItemEvent event)
	{
		OITCPlayer p = OITCPlayer.getPlayer(event.getPlayer());
		if(!p.isPlaying()) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = HIGH)
	public void onPlayerArrowPickup(PlayerPickupArrowEvent event)
	{
		OITCPlayer p = OITC.getPlayer(event.getPlayer());
		if(!p.isPlaying()) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = HIGH)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		OITCPlayer p = OITC.getPlayer(event.getEntity());
		if(!p.isPlaying()) return;

		event.setDroppedExp(0);
		event.setKeepInventory(false);
		event.setDeathMessage(null);

		handlePlayerDeath(p, null, null);
	}

	@EventHandler(priority = HIGH)
	public void onEntityDamageEvent(EntityDamageEvent event)
	{
		if (event.getEntityType() != EntityType.PLAYER) return;
		OITCPlayer p = OITCPlayer.getPlayer((Player) event.getEntity());
		if(!p.isPlaying()) return;

		if(event instanceof EntityDamageByEntityEvent)
		{
			onPlayerDamageByEntity((EntityDamageByEntityEvent) event, p);
			return;
		}

		if(event instanceof EntityDamageByBlockEvent)
		{
			onPlayerDamageByBlock((EntityDamageByBlockEvent) event, p);
			return;
		}

		EntityDamageEvent.DamageCause cause = event.getCause();

		if(
				cause != EntityDamageEvent.DamageCause.PROJECTILE
		)
		{
			if(p.getHealth() - event.getDamage() <= 0)
			{
				event.setCancelled(true);
				handlePlayerDeath(p, null, null);
			}
		}
	}

	protected void onPlayerDamageByBlock(EntityDamageByBlockEvent event, OITCPlayer p)
	{
		double damage = event.getFinalDamage();

		if(p.getHealth() - damage <= 0)
		{
			event.setCancelled(true);
			handlePlayerDeath(p, null, null);
		}
	}

	protected void onPlayerDamageByEntity(EntityDamageByEntityEvent event, OITCPlayer victim)
	{
		if(event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)
		{
			event.setCancelled(true);
			return;
		}

		if(victim.getHealth() - event.getFinalDamage() <= 0)
		{
			event.setCancelled(true);

			if(event.getDamager() instanceof Player)
			{
				OITCPlayer attacker = OITCPlayer.getPlayer((Player) event.getDamager());
				if(victim.getMatchId() == attacker.getMatchId())
				{
					handlePlayerDeath(victim, attacker, KillType.SWORD);
					return;
				}
			}

			handlePlayerDeath(victim, null, null);
		}
	}

	@EventHandler(priority = HIGH)
	public void onProjectileHit(ProjectileHitEvent event)
	{
		if(!(event.getHitEntity() instanceof Player) || !(event.getEntity().getShooter() instanceof Player)) return;

		OITCPlayer victim = OITCPlayer.getPlayer((Player) event.getHitEntity());
		OITCPlayer attacker = OITCPlayer.getPlayer((Player) event.getEntity().getShooter());

		if((!victim.isPlaying() || !attacker.isPlaying()) || (victim.getMatchId() != attacker.getMatchId())) return;

		Projectile proj = event.getEntity();
		proj.setBounce(false);
		proj.remove();

		handlePlayerDeath(victim, attacker, KillType.PROJECTILE);
	}

	@EventHandler(priority = HIGH)
	public void onBlockBreak(BlockBreakEvent event)
	{
		OITCPlayer p = OITCPlayer.getPlayer(event.getPlayer());
		if(!p.isPlaying()) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = HIGH)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		OITCPlayer p = OITCPlayer.getPlayer(event.getPlayer());
		if(!p.isPlaying()) return;

		event.setCancelled(true);
	}

	private enum KillType { PROJECTILE, SWORD, UNKNOWN }

	private void handlePlayerDeath(OITCPlayer victim, OITCPlayer attacker, KillType killType)
	{
		if(oitc.settings().explodeOnDeath())
		{
			Location deathLoc = victim.getLocation();
			deathLoc.getWorld().createExplosion(deathLoc, 0.0f);
		}

		victim.incrementDeaths();
		new ArenaPlayerState(victim, victim.getMatch().getArena().getRandomSpawn()).saveToPlayer();
		if(attacker == null) return;

		attacker.incrementKills();

		switch (killType)
		{
			case PROJECTILE:
				attacker.getInventory().addItem(new ItemStack(Material.ARROW, oitc.settings().getBowKillArrowReward()));
				break;
			case SWORD:
				attacker.getInventory().addItem(new ItemStack(Material.ARROW, oitc.settings().getSwordKillArrowReward()));
		}
	}
}

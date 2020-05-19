package lv.kaneps.oitc.player;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ArenaPlayerState extends PlayerStateSave
{
	public ArenaPlayerState(OITCPlayer player, Location location)
	{
		super(player);

		displayName = player.getDisplayName();
		this.location = location;
		fallDistance = 0;
		flying = false;
		walkSpeed = 0.20000000298023224f;
		flySpeed = 0.10000000149011612f;
		velocity = new Vector();
		gliding = false;
		glowing = false;
		invulnerable = false;
		health = 20.0d;
		remainingAir = 0;
		saturation = 0;
		foodLevel = 20;
		expLevel = 0;
		exp = 0;
		exhaustion = 0;
		fireTicks = -20;
		noDamageTicks = 0;
		gameMode = GameMode.SURVIVAL;
		helmet = null;
		chestplate = null;
		leggings = null;
		boots = null;
		offhand = null;
		extra = new ItemStack[0];
		storageContents = new ItemStack[]{ new ItemStack(Material.STONE_SWORD, 1), new ItemStack(Material.BOW, 1), new ItemStack(Material.ARROW, 1) };
		heldItemSlot = 1;
	}

	@Override
	public void save() { }

	@Override
	public boolean load() { return true; }

	@Override
	public void deleteSave() { }
}

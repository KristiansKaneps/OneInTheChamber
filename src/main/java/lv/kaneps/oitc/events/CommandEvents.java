package lv.kaneps.oitc.events;

import lv.kaneps.oitc.OITC;
import lv.kaneps.oitc.player.OITCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandEvents implements Listener
{
	protected final OITC oitc;

	public CommandEvents(OITC oitc)
	{
		this.oitc = oitc;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		Player _p = event.getPlayer();
		OITCPlayer p = OITCPlayer.getPlayer(_p);

		if(!p.isPlaying())
			return;

		String command = event.getMessage().split(" ")[0];

		boolean allow = false;

		if(command.startsWith("/"))
			command = command.substring(1);

		for(String allowedCmd : oitc.settings().getAllowedCommands())
		{
			if(allowedCmd.equalsIgnoreCase(command))
			{
				allow = true;
				break;
			}
		}

		if(!allow)
		{
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "This command is blocked while in arena!");
		}
	}
}

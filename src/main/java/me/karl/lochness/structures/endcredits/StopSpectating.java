package me.karl.lochness.structures.endcredits;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class StopSpectating implements Listener {

    @EventHandler
    public void onStop(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(MessageHandler.armorStand == null)
            return;
        if(player.getWorld().getEnvironment() != MessageHandler.armorStand.getWorld().getEnvironment())
            return;
        if(player.getLocation().distance(MessageHandler.armorStand.getLocation()) > 1)
            return;

        player.setSpectatorTarget(MessageHandler.armorStand);
    }

}

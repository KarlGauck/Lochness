package me.karl.lochness.entities.lochness;

import me.karl.lochness.Lochness;
import me.karl.lochness.structures.cave.InteractionEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class RegenEvent implements Listener {

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player))
            return;
        // if lochness released
        if (InteractionEvent.doorLoc.getBlock().getType() != Material.WATER)
            return;

        // if in structure
        Location loc = entity.getLocation();
        if(loc.getBlockX() < 544 || loc.getBlockX() > 544 + (48 * 3))
            return;
        if(loc.getBlockZ() < 144 || loc.getBlockZ() > 144 + (48 * 3))
            return;

        event.setCancelled(true);
    }

}

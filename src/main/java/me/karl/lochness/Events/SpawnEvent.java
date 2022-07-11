package me.karl.lochness.Events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class SpawnEvent implements Listener {

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        Location loc = entity.getLocation();
        if(loc.getWorld().getEnvironment() != World.Environment.THE_END)
            return;
        if(!(entity instanceof Enderman))
            return;

        event.setCancelled(true);
    }

}

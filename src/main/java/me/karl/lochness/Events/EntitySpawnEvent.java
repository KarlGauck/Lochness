package me.karl.lochness.Events;

import me.karl.lochness.Lochness;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntitySpawnEvent implements Listener {

    @EventHandler
    public void onEntitySpawn(org.bukkit.event.entity.EntitySpawnEvent event) {
        Location l = event.getLocation();
        if(!l.getWorld().equals(Lochness.getRoomLoc().getWorld()))
            return;
        if(l.getX() < Lochness.getRoomLoc().getX() || l.getX() > Lochness.getRoomLoc().getX() + 48)
            return;
        if(l.getY() < Lochness.getRoomLoc().getY() || l.getY() > Lochness.getRoomLoc().getY() + 23)
            return;
        if(l.getZ() < Lochness.getRoomLoc().getZ() || l.getZ() > Lochness.getRoomLoc().getZ() + 48)
            return;

        if(event.getEntityType() != EntityType.ZOMBIFIED_PIGLIN && event.getEntityType() != EntityType.STRIDER && event.getEntityType() != EntityType.HOGLIN)
            return;

        event.setCancelled(true);
    }

}

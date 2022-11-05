package me.karl.lochness.Events;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;

public class BrutePortalEvent implements Listener {

    @EventHandler
    public void onPortalTeleport(EntityPortalEvent event) {
        if (event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL &&
                (event.getEntity().getType() == EntityType.PIGLIN_BRUTE || event.getEntity().getType() == EntityType.HOGLIN))
            ((LivingEntity)event.getEntity()).setRemoveWhenFarAway(false);
    }

}

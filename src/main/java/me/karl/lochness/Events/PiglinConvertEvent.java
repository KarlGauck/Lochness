package me.karl.lochness.Events;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;

public class PiglinConvertEvent implements Listener {

    @EventHandler
    public void onConversion(EntityTransformEvent event) {
        if(event.getEntity().getType() == EntityType.PIGLIN_BRUTE) {
            event.setCancelled(true);
            PiglinBrute brute = (PiglinBrute) event.getEntity();
            brute.setConversionTime(Integer.MAX_VALUE);
        }
    }

}

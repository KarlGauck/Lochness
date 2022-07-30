package me.karl.lochness.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntityDeathEvent implements Listener {

    @EventHandler
    public void onDeath(org.bukkit.event.entity.EntityDeathEvent event) {
        if (event.getEntity().getScoreboardTags().contains("waterMonster"))
            event.getDrops().clear();
    }

}

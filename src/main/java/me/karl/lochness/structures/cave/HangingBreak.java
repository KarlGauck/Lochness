package me.karl.lochness.structures.cave;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

public class HangingBreak implements Listener {

    @EventHandler
    public void onBreak(HangingBreakByEntityEvent event) {
        Entity e = event.getEntity();
        if(e.getType() != EntityType.ITEM_FRAME)
            return;

        if(e.getScoreboardTags().contains("defense1"))
            event.setCancelled(true);
        if(e.getScoreboardTags().contains("defense2"))
            event.setCancelled(true);
        if(e.getScoreboardTags().contains("strength1"))
            event.setCancelled(true);
        if(e.getScoreboardTags().contains("strength2"))
            event.setCancelled(true);
        if(e.getScoreboardTags().contains("regeneration1"))
            event.setCancelled(true);
        if(e.getScoreboardTags().contains("regeneration2"))
            event.setCancelled(true);
        if(e.getScoreboardTags().contains("key1"))
            event.setCancelled(true);
        if(e.getScoreboardTags().contains("key2"))
            event.setCancelled(true);
        if(e.getScoreboardTags().contains("key3"))
            event.setCancelled(true);
        if(e.getScoreboardTags().contains("key4"))
            event.setCancelled(true);
        if(e.getScoreboardTags().contains("key5"))
            event.setCancelled(true);
    }

}

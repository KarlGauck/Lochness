package me.karl.lochness.entities.ceberos;

import me.karl.lochness.entities.LochnessEntity;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class CeberosTriggerEvent implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if(player.getGameMode() == GameMode.CREATIVE)
            return;

        if(player.getGameMode() == GameMode.SPECTATOR)
            return;

        if(player.isSneaking())
            return;

        for(LochnessEntity entity: LochnessEntity.getEntities()) {
            if(!(entity instanceof Cerberus))
                continue;

            Cerberus cerberus = (Cerberus) entity;
            cerberus.trigger(player);
        }
    }

    @EventHandler
    public void onEntityDamadge(EntityDamageByEntityEvent event) {
        Entity damadger = event.getDamager();
        Entity target = event.getEntity();

        if(!(damadger instanceof Player))
            return;

        Player player = (Player) damadger;

        if(player.getGameMode() == GameMode.CREATIVE)
            return;

        if(player.getGameMode() == GameMode.SPECTATOR)
            return;

        if(!(target instanceof Wolf))
            return;

        Wolf wolf = (Wolf) target;
        if(!wolf.getScoreboardTags().contains("ceberos"))
            return;

        for(LochnessEntity entity: LochnessEntity.getEntities()) {
            if(!(entity instanceof Cerberus))
                break;

            Cerberus cerberus = (Cerberus) entity;
            cerberus.trigger(player);
        }
    }

}

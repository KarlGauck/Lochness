package me.karl.lochness.Events;

import me.karl.lochness.enchantments.poseidonspower.CustomEnchants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Trident;
import org.bukkit.entity.Zoglin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.Currency;

public class TridentWaterEvent implements Listener {

    @EventHandler
    public void onHit(ProjectileHitEvent event) {

        Entity entity = event.getEntity();
        if(!(entity instanceof Trident))
            return;
        Trident trident = (Trident) entity;
        if(!trident.getItem().getItemMeta().hasEnchant(CustomEnchants.POSEIDONS_POWER))
            return;
        if(event.getHitEntity() != null && event.getHitEntity().getType() == EntityType.ZOGLIN) {
            event.getHitEntity().addScoreboardTag("hitByPoseidon");
            event.getEntity().addScoreboardTag("hit");
            return;
        }
        if(event.getEntity().getScoreboardTags().contains("hit")) {
            return;
        }

        trident.getLocation().getBlock().setType(Material.WATER);
    }

}

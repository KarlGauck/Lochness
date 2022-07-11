package me.karl.lochness.Events;

import me.karl.lochness.PluginUtils;
import me.karl.lochness.structures.cave.InteractionEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.*;

public class UseEvent implements Listener {

    @EventHandler
    public void onUse(PlayerItemConsumeEvent event) {

        // if lochness released
        if (InteractionEvent.doorLoc.getBlock().getType() != Material.WATER)
            return;

        // if in structure
        Location loc = event.getPlayer().getLocation();
        if(loc.getBlockX() < 544 || loc.getBlockX() > 544 + (48 * 3))
            return;
        if(loc.getBlockZ() < 144 || loc.getBlockZ() > 144 + (48 * 3))
            return;

        ItemStack stack = event.getItem();

        switch(stack.getType()) {
            case GOLDEN_APPLE:
            case ENCHANTED_GOLDEN_APPLE:
                PluginUtils.grantAdvancement(event.getPlayer(), "lochness:listened_properly");
                event.setCancelled(true);
                break;
            case POTION:
                PotionMeta potionMeta = (PotionMeta) stack.getItemMeta();
                PotionData data = potionMeta.getBasePotionData();
                if(data.getType() == PotionType.REGEN) {
                    PluginUtils.grantAdvancement(event.getPlayer(), "lochness:listened_properly");
                    event.setCancelled(true);
                }
                if(data.getType() == PotionType.INSTANT_HEAL) {
                    PluginUtils.grantAdvancement(event.getPlayer(), "lochness:listened_properly");
                    event.setCancelled(true);
                }
                break;
        }
    }

}

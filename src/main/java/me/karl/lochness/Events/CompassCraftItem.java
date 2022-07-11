package me.karl.lochness.Events;

import me.karl.lochness.Lochness;
import me.karl.lochness.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class CompassCraftItem implements Listener {

    @EventHandler
    public void onCraftPrepare(PrepareItemCraftEvent event) {

        COMPASS: {
            if(event.getRecipe() == null)
                break COMPASS;

            if(!event.getRecipe().getResult().equals(Lochness.compassRecipe.getResult()))
                break COMPASS;

            boolean customModelData1 = false;
            boolean customModelData2 = false;
            boolean customModelData3 = false;
            boolean customModelData4 = false;

            CraftingInventory inventory = event.getInventory();
            for(ItemStack stack: inventory.getContents()) {
                if(stack.hasItemMeta() && stack.getItemMeta().hasCustomModelData() && stack.getItemMeta().getCustomModelData() == 1)
                    customModelData1 = true;
                if(stack.hasItemMeta() && stack.getItemMeta().hasCustomModelData() && stack.getItemMeta().getCustomModelData() == 2)
                    customModelData2 = true;
                if(stack.hasItemMeta() && stack.getItemMeta().hasCustomModelData() && stack.getItemMeta().getCustomModelData() == 3)
                    customModelData3 = true;
                if(stack.hasItemMeta() && stack.getItemMeta().hasCustomModelData() && stack.getItemMeta().getCustomModelData() == 4)
                    customModelData4 = true;
            }

            if(!customModelData1 || !customModelData2 || !customModelData3 || !customModelData4) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
                break COMPASS;
            }
        }

    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if(event.getRecipe().getResult().equals(Lochness.compassRecipe.getResult())) {
            if (event.getWhoClicked() instanceof Player) {
                PluginUtils.grantAdvancement((Player) event.getWhoClicked(), "lochness:the_right_direction");
            }
        }

        if(event.getRecipe().getResult().equals(Lochness.goldenCodRecipe.getResult())) {
            if (event.getWhoClicked() instanceof Player) {
                PluginUtils.grantAdvancement((Player) event.getWhoClicked(), "lochness:precious_fish");
            }
        }
    }

}

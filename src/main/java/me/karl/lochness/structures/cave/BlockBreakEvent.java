package me.karl.lochness.structures.cave;

import me.karl.lochness.PluginUtils;
import me.karl.lochness.entities.LochnessEntity;
import me.karl.lochness.entities.lochness.LochnessBoss;
import me.karl.lochness.entities.watermonsters.hai.LochnessHai;
import me.karl.lochness.entities.watermonsters.hammerhai.LochnessHammerhai;
import me.karl.lochness.entities.watermonsters.krabbe.LochnessKrabbe;
import me.karl.lochness.entities.watermonsters.kraken.LochnessKraken;
import me.karl.lochness.entities.watermonsters.narwal.LochnessNarwal;
import me.karl.lochness.entities.watermonsters.orca.LochnessOrca;
import me.karl.lochness.entities.watermonsters.piranha.LochnessPiranha;
import me.karl.lochness.entities.watermonsters.rochen.LochnessRochen;
import me.karl.lochness.entities.watermonsters.turtle.LochnessTurtle;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class BlockBreakEvent implements Listener {

    public static final double REGENERATION_1 = 0.8;
    public static final double REGENERATION_2 = 0.5;
    public static final double REGENERATION_3 = 0.3;

    public static final double STRENGTH_FACTOR_1 = 1.7;
    public static final double STRENGTH_FACTOR_2 = 1.5;
    public static final double STRENGTH_FACTOR_3 = 1.3;

    public static final double DEFENSE_FACTOR_1 = 0.3;
    public static final double DEFENSE_FACTOR_2 = 0.5;
    public static final double DEFENSE_FACTOR_3 = 0.8;

    public static boolean regeneration_1_broken = false;
    public static boolean regeneration_2_broken = false;
    public static boolean regeneration_3_broken = false;

    public static boolean strength_1_broken = false;
    public static boolean strength_2_broken = false;
    public static boolean strength_3_broken = false;

    public static boolean defense_1_broken = false;
    public static boolean defense_2_broken = false;
    public static boolean defense_3_broken = false;

    @EventHandler
    public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent event) {
        Player p = event.getPlayer();
        Block block = event.getBlock();
        Location loc = block.getLocation();

        if(p.getGameMode().equals(GameMode.CREATIVE))
            return;

        if(!loc.getWorld().getEnvironment().equals(World.Environment.THE_END))
            return;

        if(loc.getBlockX() < 544 || loc.getBlockX() > 544 + (48 * 3))
            return;

        if(loc.getBlockZ() < 144 || loc.getBlockZ() > 144 + (48 * 3))
            return;

        if(block.getType().equals(Material.CONDUIT)) {

            PluginUtils.grantAdvancement(event.getPlayer(), "lochness:no_magic_involved");

            ItemStack stack = new ItemStack(Material.IRON_NUGGET);
            ItemMeta meta = stack.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();
            lore.add("Insert this key in the");
            lore.add("associated itemframe to get access");
            lore.add("to the next type of monster");
            meta.setLore(lore);

            // ------------ DEFENSE ------------
            meta.setCustomModelData(6);
            if(loc.toVector().equals(CaveLogic.defense1Cond.getBlock().getLocation().toVector())) {
                if(!LochnessEntity.isEntityAlive(LochnessKrabbe.class)) {
                    LochnessBoss.DEFENSE_EFFECT_FACTOR = DEFENSE_FACTOR_2;
                    meta.setDisplayName("" + ChatColor.RESET + ChatColor.BOLD + ChatColor.YELLOW + "Defense #1");
                    stack.setItemMeta(meta);
                    block.getWorld().dropItem(loc, stack);
                    defense_1_broken = true;
                    CaveLogic.resetDisplay();
                    return;
                }
            }
            if(loc.toVector().equals(CaveLogic.defense2Cond.getBlock().getLocation().toVector())) {
                if(!LochnessEntity.isEntityAlive(LochnessOrca.class)) {
                    LochnessBoss.DEFENSE_EFFECT_FACTOR = DEFENSE_FACTOR_3;
                    meta.setDisplayName("" + ChatColor.RESET + ChatColor.BOLD + ChatColor.YELLOW + "Defense #2");
                    stack.setItemMeta(meta);
                    block.getWorld().dropItem(loc, stack);
                    defense_2_broken = true;
                    CaveLogic.resetDisplay();
                    return;
                }
            }
            if(loc.toVector().equals(CaveLogic.defense3Cond.getBlock().getLocation().toVector())) {
                if(!LochnessEntity.isEntityAlive(LochnessTurtle.class)) {
                    LochnessBoss.DEFENSE_EFFECT_FACTOR = 1;
                    block.getWorld().dropItem(loc, new ItemStack(Material.NETHERITE_INGOT));
                    defense_3_broken = true;
                    CaveLogic.resetDisplay();
                    return;
                }
            }

            // ------------ REGENERATION ------------
            meta.setCustomModelData(7);
            if(loc.toVector().equals(CaveLogic.regeneration1Cond.getBlock().getLocation().toVector())) {
                if(!LochnessEntity.isEntityAlive(LochnessKraken.class)) {
                    LochnessBoss.REGENERATION_EFFECT = REGENERATION_2;
                    meta.setDisplayName("" + ChatColor.RESET + ChatColor.BOLD + ChatColor.BLUE + "Regeneration #1");
                    stack.setItemMeta(meta);
                    block.getWorld().dropItem(loc, stack);
                    regeneration_1_broken = true;
                    CaveLogic.resetDisplay();
                    return;
                }
            }
            if(loc.toVector().equals(CaveLogic.regeneration2Cond.getBlock().getLocation().toVector())) {
                if(!LochnessEntity.isEntityAlive(LochnessRochen.class)) {
                    LochnessBoss.REGENERATION_EFFECT = REGENERATION_3;
                    meta.setDisplayName("" + ChatColor.RESET + ChatColor.BOLD + ChatColor.BLUE + "Regeneration #2");
                    stack.setItemMeta(meta);
                    block.getWorld().dropItem(loc, stack);
                    regeneration_2_broken = true;
                    CaveLogic.resetDisplay();
                    return;
                }
            }
            if(loc.toVector().equals(CaveLogic.regeneration3Cond.getBlock().getLocation().toVector())) {
                if(!LochnessEntity.isEntityAlive(LochnessNarwal.class)) {
                    LochnessBoss.REGENERATION_EFFECT = 0;
                    block.getWorld().dropItem(loc, new ItemStack(Material.NETHERITE_INGOT));
                    regeneration_3_broken = true;
                    CaveLogic.resetDisplay();
                    return;
                }
            }

            // ------------ STRENGTH ------------
            meta.setCustomModelData(8);
            if(loc.toVector().equals(CaveLogic.strength1Cond.getBlock().getLocation().toVector())) {
                if(!LochnessEntity.isEntityAlive(LochnessPiranha.class)) {
                    LochnessBoss.STRENGTH_EFFECT_FACTOR = STRENGTH_FACTOR_2;
                    meta.setDisplayName("" + ChatColor.RESET + ChatColor.BOLD + ChatColor.DARK_RED + "Strength #1");
                    stack.setItemMeta(meta);
                    block.getWorld().dropItem(loc, stack);
                    strength_1_broken = true;
                    CaveLogic.resetDisplay();
                    return;
                }
            }
            if(loc.toVector().equals(CaveLogic.strength2Cond.getBlock().getLocation().toVector())) {
                if(!LochnessEntity.isEntityAlive(LochnessHammerhai.class)) {
                    LochnessBoss.STRENGTH_EFFECT_FACTOR = STRENGTH_FACTOR_3;
                    meta.setDisplayName("" + ChatColor.RESET + ChatColor.BOLD + ChatColor.DARK_RED + "Strength #2");
                    stack.setItemMeta(meta);
                    block.getWorld().dropItem(loc, stack);
                    strength_2_broken = true;
                    CaveLogic.resetDisplay();
                    return;
                }
            }
            if(loc.toVector().equals(CaveLogic.strength3Cond.getBlock().getLocation().toVector())) {
                if(!LochnessEntity.isEntityAlive(LochnessHai.class)) {
                    LochnessBoss.STRENGTH_EFFECT_FACTOR = 1;
                    block.getWorld().dropItem(loc, new ItemStack(Material.NETHERITE_INGOT));
                    strength_3_broken = true;
                    CaveLogic.resetDisplay();
                    return;
                }
            }

        }

        event.setCancelled(true);
    }

    public static void initValues() {
        defense_1_broken = CaveLogic.defense1Cond.getBlock().getType() != Material.CONDUIT;
        defense_2_broken = CaveLogic.defense2Cond.getBlock().getType() != Material.CONDUIT;
        defense_3_broken = CaveLogic.defense3Cond.getBlock().getType() != Material.CONDUIT;
        regeneration_1_broken = CaveLogic.regeneration1Cond.getBlock().getType() != Material.CONDUIT;
        regeneration_2_broken = CaveLogic.regeneration2Cond.getBlock().getType() != Material.CONDUIT;
        regeneration_3_broken = CaveLogic.regeneration3Cond.getBlock().getType() != Material.CONDUIT;
        strength_1_broken = CaveLogic.strength1Cond.getBlock().getType() != Material.CONDUIT;
        strength_2_broken = CaveLogic.strength2Cond.getBlock().getType() != Material.CONDUIT;
        strength_3_broken = CaveLogic.strength3Cond.getBlock().getType() != Material.CONDUIT;
    }

}

package me.karl.lochness.structures.cave;

import me.karl.lochness.Lochness;
import me.karl.lochness.entities.LochnessEntity;
import me.karl.lochness.entities.lochness.LochnessBoss;
import me.karl.lochness.entities.watermonsters.WaterMonster;
import me.karl.lochness.entities.watermonsters.hai.LochnessHai;
import me.karl.lochness.entities.watermonsters.hammerhai.LochnessHammerhai;
import me.karl.lochness.entities.watermonsters.krabbe.LochnessKrabbe;
import me.karl.lochness.entities.watermonsters.kraken.LochnessKraken;
import me.karl.lochness.entities.watermonsters.narwal.LochnessNarwal;
import me.karl.lochness.entities.watermonsters.orca.LochnessOrca;
import me.karl.lochness.entities.watermonsters.piranha.LochnessPiranha;
import me.karl.lochness.entities.watermonsters.rochen.LochnessRochen;
import me.karl.lochness.entities.watermonsters.turtle.LochnessTurtle;
import me.karl.lochness.structures.StructureLoader;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;

public class CaveLogic {

    public static final File generated = new File("./" + Bukkit.getWorlds().get(0).getName() + "/cavegenerated");

    public static final Location regeneration3 = new Location(Bukkit.getWorlds().get(2), 640, 52, 150);
    public static final Location regeneration3Cond = new Location(Bukkit.getWorlds().get(2), 644, 52, 146);
    public static final Location regeneration3Display = new Location(Bukkit.getWorlds().get(2), 603, 57, 187);

    public static final Location regeneration2 = new Location(Bukkit.getWorlds().get(2), 562, 41, 185);
    public static final Location regeneration2Cond = new Location(Bukkit.getWorlds().get(2), 563, 41, 190);
    public static final Location regeneration2Display = new Location(Bukkit.getWorlds().get(2), 603, 56, 187);

    public static final Location regeneration1 = new Location(Bukkit.getWorlds().get(2), 622, 54, 167);
    public static final Location regeneration1Cond = new Location(Bukkit.getWorlds().get(2), 618, 54, 167);
    public static final Location regeneration1Display = new Location(Bukkit.getWorlds().get(2), 603, 55, 187);

    public static final Location defense3 = new Location(Bukkit.getWorlds().get(2), 629, 51, 154);
    public static final Location defense3Cond = new Location(Bukkit.getWorlds().get(2), 629, 51, 149);
    public static final Location defense3Display = new Location(Bukkit.getWorlds().get(2), 601, 57, 187);

    public static final Location defense2 = new Location(Bukkit.getWorlds().get(2), 579, 42, 274);
    public static final Location defense2Cond = new Location(Bukkit.getWorlds().get(2), 579, 42, 282);
    public static final Location defense2Display = new Location(Bukkit.getWorlds().get(2), 601, 56, 187);

    public static final Location defense1 = new Location(Bukkit.getWorlds().get(2), 602, 32, 159);
    public static final Location defense1Cond = new Location(Bukkit.getWorlds().get(2), 607, 32, 159);
    public static final Location defense1Display = new Location(Bukkit.getWorlds().get(2), 601, 55, 187);

    public static final Location strength3 = new Location(Bukkit.getWorlds().get(2), 583, 37, 278);
    public static final Location strength3Cond = new Location(Bukkit.getWorlds().get(2), 583, 37, 286);
    public static final Location strength3Display = new Location(Bukkit.getWorlds().get(2), 605, 57, 187);

    public static final Location strength2 = new Location(Bukkit.getWorlds().get(2), 574, 38, 274);
    public static final Location strength2Cond = new Location(Bukkit.getWorlds().get(2), 574, 38, 281);
    public static final Location strength2Display = new Location(Bukkit.getWorlds().get(2), 605, 56, 187);

    public static final Location strength1 = new Location(Bukkit.getWorlds().get(2), 588, 45, 174);
    public static final Location strength1Cond = new Location(Bukkit.getWorlds().get(2), 588, 45, 179);
    public static final Location strength1Display = new Location(Bukkit.getWorlds().get(2), 605, 55, 187);

    public static final Location lochnessSpawnLocation = new Location(Bukkit.getWorlds().get(2), 560, 45, 210);

    public static final int OCTOPUS_COUNT = 3;
    public static final int HAI_COUNT = 4;
    public static final int HAMMERHAI_COUNT = 3;
    public static final int TURTLE_COUNT = 1;
    public static final int RAY_COUNT = 5;
    public static final int PIRANHA_COUNT = 20;
    public static final int ORCA_COUNT = 5;
    public static final int NARWAL_COUNT = 3;
    public static final int KRABBE_COUNT = 8;

    public static void generateTotalCave() {

        if (!generated.exists()) {
            try {
                generated.createNewFile();
                resetCave();
            } catch (IOException e) {
                Bukkit.broadcastMessage(Lochness.getPrefix() + "Cave can't be generated properly, features might be broken");
            }
        } else {
            BlockBreakEvent.initValues();
        }

    }

    public static void resetCave() {
        StructureLoader.generateCave();
        Bukkit.getScheduler().runTaskLater(Lochness.getPlugin(), () -> {
            if(generated.exists()) {
                StructureLoader.generateIslands();

                // Delete all ItemFrames with keys
                for(Entity e: Bukkit.getWorlds().get(2).getEntities()) {
                    if(e.getType() != EntityType.ITEM_FRAME)
                        continue;

                    ItemStack stack = ((ItemFrame)e).getItem();
                    if(stack == null)
                        continue;

                    if(stack.getType() != Material.IRON_NUGGET)
                        continue;
                    if(stack.hasItemMeta() && stack.getItemMeta().hasCustomModelData()  && stack.getItemMeta().getCustomModelData() != 0) {
                        e.remove();
                    }
                }

                // Delete all items with keys
                for(Entity e: Bukkit.getWorlds().get(2).getEntities()) {
                    if(e.getType() != EntityType.DROPPED_ITEM)
                        continue;

                    ItemStack stack = ((Item)e).getItemStack();
                    if(stack.getType() != Material.IRON_NUGGET)
                        continue;
                    if(stack.hasItemMeta() && stack.getItemMeta().hasCustomModelData()  && stack.getItemMeta().getCustomModelData() != 0) {
                        e.remove();
                    }
                }

                // remove all keys out of player inventories
                for(Player p: Bukkit.getWorlds().get(2).getPlayers()) {
                    Inventory i = p.getInventory();
                    for(int ind = i.getContents().length-1; ind >= 0; ind--) {
                        ItemStack s = i.getContents()[ind];
                        if(s == null)
                            continue;
                        if(s.getType() != Material.IRON_NUGGET)
                            continue;
                        if(s.hasItemMeta() && s.getItemMeta().hasCustomModelData() && s.getItemMeta().getCustomModelData() != 0) {
                            i.remove(s);
                        }
                    }
                }

                for(Player p: Bukkit.getWorlds().get(2).getPlayers()) {
                    Location loc = p.getLocation();
                    if(!loc.getWorld().getEnvironment().equals(World.Environment.THE_END))
                        continue;

                    if(loc.getBlockX() < 544 || loc.getBlockX() > 544 + (48 * 3))
                        continue;

                    if(loc.getBlockZ() < 144 || loc.getBlockZ() > 144 + (48 * 3))
                        continue;

                    p.teleport(Lochness.getIslandLoc().add(new Vector(0, 67, 0)));
                }
            }
            resetEntities();
            spawnEntities();
            BlockBreakEvent.initValues();
            LochnessBoss.resetEffectValues();
            InteractionEvent.interacted = false;
        },2);
    }

    public static void spawnEntities() {
        // --- Spawn LochnessBoss ---
        new LochnessBoss(lochnessSpawnLocation);

        // --- Spawn Kraken ---
        for (int i = 0; i < OCTOPUS_COUNT; i++) {
            new LochnessKraken(regeneration1);
        }

        // --- Spawn Turtle ---
        for (int i = 0; i < TURTLE_COUNT; i++) {
            new LochnessTurtle(defense3);
        }

        // --- Spawn Narwhal ---
        for (int i = 0; i < NARWAL_COUNT; i++) {
            new LochnessNarwal(regeneration3);
        }

        // --- Spawn Rays ---
        for (int i = 0; i < RAY_COUNT; i++) {
            new LochnessRochen(regeneration2);
        }

        // --- Spawn Piranhas ---
        for (int i = 0; i < PIRANHA_COUNT; i++) {
            new LochnessPiranha(strength1);
        }

        // --- Spawn Krabbe ---
        for (int i = 0; i < KRABBE_COUNT; i++) {
            new LochnessKrabbe(defense1);
        }

        // --- Spawn Hammerhai ---
        for (int i = 0; i < HAMMERHAI_COUNT; i++) {
            new LochnessHammerhai(strength2);
        }

        // --- Spawn Orca ---
        for (int i = 0; i < ORCA_COUNT; i++) {
            new LochnessOrca(defense2);
        }

        // --- Spawn Hai ---
        for (int i = 0; i < HAI_COUNT; i++) {
            new LochnessHai(strength3);
        }
    }

    public static void resetEntities() {
        for (LochnessEntity e : LochnessEntity.getEntities()) {
            if (e instanceof WaterMonster)
                e.remove();
            if (e instanceof LochnessBoss)
                e.remove();
        }
    }

    public static void particleLoop() {

        regeneration1Cond.getWorld().spawnParticle(Particle.HEART, regeneration1Cond.clone().add(new Vector(0.5, 0.5, 0.5)), 3, 0.2, 0.2, 0.2, 0.02);
        regeneration2Cond.getWorld().spawnParticle(Particle.HEART, regeneration2Cond.clone().add(new Vector(0.5, 0.5, 0.5)), 3, 0.2, 0.2, 0.2, 0.02);
        regeneration3Cond.getWorld().spawnParticle(Particle.HEART, regeneration3Cond.clone().add(new Vector(0.5, 0.5, 0.5)), 3, 0.2, 0.2, 0.2, 0.02);
        strength1Cond.getWorld().spawnParticle(Particle.CRIT, strength1Cond.clone().add(new Vector(0.5, 0.5, 0.5)), 3, 0.2, 0.2, 0.2, 0.02);
        strength2Cond.getWorld().spawnParticle(Particle.CRIT, strength2Cond.clone().add(new Vector(0.5, 0.5, 0.5)), 3, 0.2, 0.2, 0.2, 0.02);
        strength3Cond.getWorld().spawnParticle(Particle.CRIT, strength3Cond.clone().add(new Vector(0.5, 0.5, 0.5)), 3, 0.2, 0.2, 0.2, 0.02);
        strength1Cond.getWorld().spawnParticle(Particle.NAUTILUS, defense1Cond.clone().add(new Vector(0.5, 0.5, 0.5)), 3, 0.2, 0.2, 0.2, 0.02);
        strength2Cond.getWorld().spawnParticle(Particle.NAUTILUS, defense2Cond.clone().add(new Vector(0.5, 0.5, 0.5)), 3, 0.2, 0.2, 0.2, 0.02);
        strength3Cond.getWorld().spawnParticle(Particle.NAUTILUS, defense3Cond.clone().add(new Vector(0.5, 0.5, 0.5)), 3, 0.2, 0.2, 0.2, 0.02);

        Location portal = new Location(Bukkit.getWorlds().get(2), 676, 68, 206);
        portal.getWorld().spawnParticle(Particle.CLOUD, portal, 100, 1, 1, 0, 0);

    }

    public static void resetDisplay() {
        if(BlockBreakEvent.regeneration_1_broken) {
            regeneration1Display.getBlock().setType(Material.LIME_CONCRETE);
        }
        if(BlockBreakEvent.regeneration_2_broken) {
            regeneration2Display.getBlock().setType(Material.LIME_CONCRETE);
        }
        if(BlockBreakEvent.regeneration_3_broken) {
            regeneration3Display.getBlock().setType(Material.LIME_CONCRETE);
        }

        if(BlockBreakEvent.defense_1_broken) {
            defense1Display.getBlock().setType(Material.YELLOW_CONCRETE);
        }
        if(BlockBreakEvent.defense_2_broken) {
            defense2Display.getBlock().setType(Material.YELLOW_CONCRETE);
        }
        if(BlockBreakEvent.defense_3_broken) {
            defense3Display.getBlock().setType(Material.YELLOW_CONCRETE);
        }

        if(BlockBreakEvent.strength_1_broken) {
            strength1Display.getBlock().setType(Material.RED_CONCRETE);
        }
        if(BlockBreakEvent.strength_2_broken) {
            strength2Display.getBlock().setType(Material.RED_CONCRETE);
        }
        if(BlockBreakEvent.strength_3_broken) {
            strength3Display.getBlock().setType(Material.RED_CONCRETE);
        }
    }

}

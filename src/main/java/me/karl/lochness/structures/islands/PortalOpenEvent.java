package me.karl.lochness.structures.islands;

import me.karl.lochness.Lochness;
import me.karl.lochness.PluginUtils;
import me.karl.lochness.enchantments.poseidonspower.CustomEnchants;
import me.karl.lochness.structures.StructureLoader;
import me.karl.lochness.structures.cave.CaveLogic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class PortalOpenEvent implements Listener {

    static Location tower1 = null;
    static Location tower2 = null;
    static Location tower3 = null;
    static Location tower4 = null;

    @EventHandler
    public void onTridentPortalEvent(EntityDamageByEntityEvent event) {

        Entity damadger = event.getDamager();
        if (!(damadger instanceof Trident))
                return;

        Trident trident = (Trident) damadger;
        if(!trident.getItem().getItemMeta().hasEnchant(CustomEnchants.POSEIDONS_POWER))
            return;

        Entity damadged = event.getEntity();
        if(!(damadged instanceof Zoglin))
            return;

        Location islandMiddle = Lochness.getIslandLoc();
        islandMiddle.setY(68);
        if(islandMiddle.toVector().add(new Vector(0, 0, 6)).distance(damadged.getLocation().toVector()) > 15)
            return;

        ((Zoglin) damadged).setHealth(0);

        tower1 = Lochness.getIslandLoc().add(new Vector(30, 68, 0));
        tower2 = Lochness.getIslandLoc().add(new Vector(-30, 68, 0));
        tower3 = Lochness.getIslandLoc().add(new Vector(0, 68, 30));
        tower4 = Lochness.getIslandLoc().add(new Vector(0, 68, -30));
        tower1.setY(84);
        tower2.setY(84);
        tower3.setY(84);
        tower4.setY(84);

        PiglinBrute brute1 = null;
        PiglinBrute brute2 = null;
        PiglinBrute brute3 = null;
        PiglinBrute brute4 = null;

        for(Entity entity: tower1.getWorld().spawnEntity(tower1, EntityType.AREA_EFFECT_CLOUD).getNearbyEntities(5, 5, 5)){
            if(entity instanceof PiglinBrute)
                brute1 = (PiglinBrute) entity;
        }

        for(Entity entity: tower2.getWorld().spawnEntity(tower2, EntityType.AREA_EFFECT_CLOUD).getNearbyEntities(5, 5, 5)){
            if(entity instanceof PiglinBrute)
                brute2 = (PiglinBrute) entity;
        }

        for(Entity entity: tower3.getWorld().spawnEntity(tower3, EntityType.AREA_EFFECT_CLOUD).getNearbyEntities(5, 5, 5)){
            if(entity instanceof PiglinBrute)
                brute3 = (PiglinBrute) entity;
        }

        for(Entity entity: tower4.getWorld().spawnEntity(tower4, EntityType.AREA_EFFECT_CLOUD).getNearbyEntities(5, 5, 5)){
            if(entity instanceof PiglinBrute)
                brute4 = (PiglinBrute) entity;
        }

        if(brute1 == null)
            return;
        if(brute2 == null)
            return;
        if(brute3 == null)
            return;
        if(brute4 == null)
            return;

        //Kill brutes
        brute1.setHealth(0);
        brute2.setHealth(0);
        brute3.setHealth(0);
        brute4.setHealth(0);

        openPortal();

        if(((Trident)damadger).getShooter() instanceof Player)
            PluginUtils.grantAdvancement((Player)((Trident)damadger).getShooter(), "lochness:an_offering");

    }

    public static void openPortal() {
        tower1 = Lochness.getIslandLoc().add(new Vector(30, 68, 0));
        tower2 = Lochness.getIslandLoc().add(new Vector(-30, 68, 0));
        tower3 = Lochness.getIslandLoc().add(new Vector(0, 68, 30));
        tower4 = Lochness.getIslandLoc().add(new Vector(0, 68, -30));
        tower1.setY(84);
        tower2.setY(84);
        tower3.setY(84);
        tower4.setY(84);

        //Summon lightning bolts
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon lightning_bolt " + tower1.getX() + " " + tower1.getY() + " " + tower1.getZ());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon lightning_bolt " + tower2.getX() + " " + tower2.getY() + " " + tower2.getZ());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon lightning_bolt " + tower3.getX() + " " + tower3.getY() + " " + tower3.getZ());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon lightning_bolt " + tower4.getX() + " " + tower4.getY() + " " + tower4.getZ());

        tower1.add(new Vector(0, 3, 0)).getBlock().setType(Material.WATER);
        tower2.add(new Vector(0, 3, 0)).getBlock().setType(Material.WATER);
        tower3.add(new Vector(0, 3, 0)).getBlock().setType(Material.WATER);
        tower4.add(new Vector(0, 3, 0)).getBlock().setType(Material.WATER);

        Location cmdL = Lochness.getIslandLoc().add(new Vector(-3, 63, 4));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fill " + cmdL.getBlockX() + " " + cmdL.getBlockY() + " " + cmdL.getBlockZ() + " "
                + (cmdL.getBlockX() + 6) + " " + cmdL.getBlockY() + " " + (cmdL.getBlockZ() + 6) + " air replace bedrock");

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement grant @a only minecraft:story/enter_the_end");
        CaveLogic.spawnEntities();
        CaveLogic.isResetingCave = false;

    }

}

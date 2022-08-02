package me.karl.lochness.structures.cave;

import me.karl.lochness.Lochness;
import me.karl.lochness.PluginUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class InteractionEvent implements Listener {

    private static ArrayList<Location> particles = new ArrayList<>();
    private static ArrayList<Location> roundParticles = new ArrayList<>();
    private static ArrayList<Integer> roundParticleTime = new ArrayList<>();
    public static Location doorLoc = new Location(Bukkit.getWorlds().get(2), 571, 49, 198);

    public static Location source;

    public static int time = 0;

    public static boolean interacted = false;

    @EventHandler
    public void onInteraction(PlayerInteractAtEntityEvent event) {
        /*
        if(interacted) {
            return;
        }
         */

        Entity entity = event.getRightClicked();

        if(entity.getScoreboardTags().contains("lochnessBell") && !interacted) {
            source = entity.getLocation().clone();

            particles.add(entity.getLocation().clone());
            time = 0;

            for(int i = 0; i < 100; i++) {
                Bukkit.getScheduler().runTaskLater(Lochness.getPlugin(), () -> {
                    roundParticles.add(entity.getLocation().clone());
                    roundParticleTime.add(0);
                }, i);
            }
            event.setCancelled(true);
            interacted = true;
            PluginUtils.grantAdvancement(event.getPlayer(), "lochness:finally_the_end");
            for(Entity e: entity.getNearbyEntities(50, 50, 50)) {
                if(e instanceof Player) {
                    ((Player)e).playSound(entity.getLocation(), Sound.BLOCK_BELL_USE, 1, 1);
                }
            }
        }

        if(entity instanceof Player) {
            Player interacting = event.getPlayer();
            Player clicked = (Player) event.getRightClicked();

            ItemStack stack = interacting.getInventory().getItemInMainHand();
            if(interacting.getScoreboardTags().contains("healed")) {
                interacting.removeScoreboardTag("healed");
                return;
            }

            if(interacting.hasCooldown(Material.STRUCTURE_BLOCK))
                return;

            if(stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().equals("Golden Cod")) {
                if(stack.getItemMeta().hasCustomModelData() && stack.getItemMeta().getCustomModelData() == 2) {
                    interacting.addScoreboardTag("healed");
                    boolean used = true;
                    if(clicked.getHealth() <= 19)
                        clicked.setHealth(clicked.getHealth() + 1);
                    else {
                        if(clicked.hasPotionEffect(PotionEffectType.ABSORPTION)) {
                            if(clicked.getPotionEffect(PotionEffectType.ABSORPTION).getAmplifier() < 0)
                                clicked.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, clicked.getPotionEffect(PotionEffectType.ABSORPTION).getAmplifier() + 1, true, true));
                            else
                                used = false;
                        }
                        else
                            clicked.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 0, true, true));
                    }

                    if(used) {
                        stack.setAmount(stack.getAmount() - 1);
                        interacting.setCooldown(Material.STRUCTURE_BLOCK, 10);
                    }

                    PluginUtils.grantAdvancement(interacting, "lochness:social_healing");
                }
            }
        }
    }

    public static void particleLoop() {
        time++;
        for(int i = roundParticles.size()-1; i >= 0; i--) {
            if(roundParticleTime.get(i) > 80) {
                roundParticleTime.remove(i);
                roundParticles.remove(i);
                continue;
            }
            roundParticleTime.set(i, roundParticleTime.get(i) + 1);
            double y = ((double)roundParticleTime.get(i) / 600) - 1;
            if(i % 2 == 0)
                y *= -1;
            double x = Math.sin((double)roundParticleTime.get(i)) * (y / Math.tan(y*Math.PI));
            double z = Math.cos((double)roundParticleTime.get(i)) * (y / Math.tan(y*Math.PI));
            Vector v = new Vector(x, y, z);
            v.normalize();
            v.multiply(Math.sqrt(time * 4));
            roundParticles.set(i, source.clone().add(v));
            roundParticles.get(i).getWorld().spawnParticle(Particle.CRIT_MAGIC, roundParticles.get(i), 10, 0, 0, 0, 0);
        }

        for(int i = particles.size()-1; i >= 0; i--) {
            if(particles.get(i).distance(doorLoc) < 2){
                openCage();
                particles.remove(i);
                continue;
            }
            particles.set(i, particles.get(i).add(doorLoc.clone().subtract(particles.get(i)).toVector().normalize().multiply(0.5)));
            particles.get(i).getWorld().spawnParticle(Particle.PORTAL, particles.get(i), 10, 0.1, 0.1, 0.1, 0.01);
        }
    }

    public static void openCage() {
        doorLoc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, doorLoc, 1, 0, 0, 0, 0);
        doorLoc.clone().add(new Vector(0, 0, 1)).getBlock().setType(Material.WATER);
        doorLoc.clone().add(new Vector(0, 1, 1)).getBlock().setType(Material.WATER);
        doorLoc.clone().add(new Vector(0, -1, 1)).getBlock().setType(Material.WATER);
        doorLoc.clone().add(new Vector(0, 0, 0)).getBlock().setType(Material.WATER);
        doorLoc.clone().add(new Vector(0, 1, 0)).getBlock().setType(Material.WATER);
        doorLoc.clone().add(new Vector(0, -1, 0)).getBlock().setType(Material.WATER);
        doorLoc.clone().add(new Vector(0, 0, -1)).getBlock().setType(Material.WATER);
        doorLoc.clone().add(new Vector(0, 1, -1)).getBlock().setType(Material.WATER);
        doorLoc.clone().add(new Vector(0, -1, -1)).getBlock().setType(Material.WATER);
        doorLoc.clone().add(new Vector(0, 0, -2)).getBlock().setType(Material.WATER);
        doorLoc.clone().add(new Vector(0, 1, -2)).getBlock().setType(Material.WATER);
        doorLoc.clone().add(new Vector(0, -1, -2)).getBlock().setType(Material.WATER);
        doorLoc.getWorld().playSound(doorLoc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
        for(int xPos = 544; xPos < 675; xPos+=17) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in minecraft:the_end run fill "
                    + xPos + " 36 223 " + (xPos + 17) + " 70 174 minecraft:shroomlight replace minecraft:dead_brain_coral_block");
        }
    }

}

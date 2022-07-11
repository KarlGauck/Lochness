package me.karl.lochness.entities.watermonsters.kraken;

import me.karl.lochness.Lochness;
import me.karl.lochness.entities.Hitbox;
import me.karl.lochness.entities.LochnessEntity;
import me.karl.lochness.entities.watermonsters.WaterMonster;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Random;

public class LochnessKraken extends WaterMonster {

    public static transient Location lastSaugLoc;

    public LochnessKraken(LochnessKraken piranha) {
        super(piranha);
    }

    public LochnessKraken(Location loc) {
        super(loc);
    }

    @Override
    protected void attackLogic() {
        super.attackLogic();

        for(Entity e: drowned.getNearbyEntities(10, 10, 10)) {
            if(!(e instanceof Player))
                continue;

            boolean nearKraken = false;
            Player p = (Player) e;

            for(LochnessEntity le: LochnessEntity.getEntities()) {
                if(!(le instanceof LochnessKraken))
                    continue;

                LochnessKraken k = (LochnessKraken) le;
                if(k.drowned.getLocation().toVector().distance(p.getLocation().toVector()) < 4)
                    nearKraken = true;
            }

            if(!nearKraken) {
                p.removeScoreboardTag("angesaugt");
            }
        }

        SPECIAL_ATTACK:
        {

            timeSinceLastSpecialAttack++;

            if (timeSinceLastSpecialAttack < SPECIAL_ATTACK_FEEDBACK + SPECIAL_ATTACK_DURATION && timeSinceLastSpecialAttack > SPECIAL_ATTACK_FEEDBACK) {
                if(!(drowned.getTarget() instanceof Player)) {
                    timeSinceLastSpecialAttack = SPECIAL_ATTACK_FEEDBACK + SPECIAL_ATTACK_DURATION;
                    drowned.teleport(lastSaugLoc);
                    for(Player p: drowned.getWorld().getPlayers()) {
                        p.removeScoreboardTag("angesaugt");
                    }
                    break SPECIAL_ATTACK;
                }

                if(!isTargetValid((Player)drowned.getTarget(), drowned.getWorld())) {
                    timeSinceLastSpecialAttack = SPECIAL_ATTACK_FEEDBACK + SPECIAL_ATTACK_DURATION;
                    drowned.teleport(lastSaugLoc);
                    for(Player p: drowned.getWorld().getPlayers()) {
                        p.removeScoreboardTag("angesaugt");
                    }
                    break SPECIAL_ATTACK;
                }
            }
            if(!(drowned.getTarget() instanceof Player)) {
                break SPECIAL_ATTACK;
            }

            if(!isTargetValid((Player)drowned.getTarget(), drowned.getWorld())) {
                break SPECIAL_ATTACK;
            }

            if (timeSinceLastSpecialAttack < SPECIAL_ATTACK_FEEDBACK) {
                drowned.getEquipment().setHelmet(getHeadStack());
            } else if (timeSinceLastSpecialAttack < SPECIAL_ATTACK_FEEDBACK + SPECIAL_ATTACK_DURATION) {
                ItemStack stack = new ItemStack(Material.STICK);
                ItemMeta meta = stack.getItemMeta();
                meta.setCustomModelData(10);
                stack.setItemMeta(meta);
                drowned.getEquipment().setHelmet(stack);
                drowned.teleport(drowned.getTarget().getEyeLocation().add(new Vector(Math.cos((double) timeLived / 12) * 0.7, -0.5, Math.sin((double) timeLived / 12) * 0.7))
                        .setDirection(drowned.getTarget().getEyeLocation().subtract(drowned.getEyeLocation()).toVector()));
                drowned.getTarget().addScoreboardTag("angesaugt");
                drowned.getTarget().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 10, false, false));
                drowned.setGravity(false);
                lastSaugLoc = drowned.getTarget().getLocation();
            } else if (timeSinceLastSpecialAttack == SPECIAL_ATTACK_FEEDBACK + SPECIAL_ATTACK_DURATION) {
                if(drowned.getTarget() != null && drowned.getTarget().getScoreboardTags().contains("angesaugt")) {
                    drowned.getTarget().removeScoreboardTag("angesaugt");
                }
                drowned.setGravity(true);
            } else {
                drowned.getEquipment().setHelmet(getHeadStack());
            }

            if (timeSinceLastSpecialAttack < SPECIAL_ATTACK_COOLDOWN)
                break SPECIAL_ATTACK;

            if (new Random().nextInt(SPECIAL_ATTACK_PROPABILITY) != 0)
                break SPECIAL_ATTACK;

            if(drowned.getTarget().getScoreboardTags().contains("angesaugt"))
                break SPECIAL_ATTACK;

            if (drowned.getLocation().distance(drowned.getTarget().getLocation()) > 1.5)
                break SPECIAL_ATTACK;

            timeSinceLastSpecialAttack = 0;
        }
    }

    public void stopSucking() {
        if(timeSinceLastSpecialAttack > SPECIAL_ATTACK_FEEDBACK && timeSinceLastSpecialAttack <= SPECIAL_ATTACK_FEEDBACK + SPECIAL_ATTACK_DURATION) {
            timeSinceLastSpecialAttack = SPECIAL_ATTACK_FEEDBACK + SPECIAL_ATTACK_DURATION;
            drowned.getTarget().removeScoreboardTag("angesaugt");
        }
    }

    public Location getBarLocation() {
        return new Location(Bukkit.getWorlds().get(2), 619, 54, 167);
    }

    public int getHIT_COOLDOWN() {
        return 20;
    }

    public int getSPECIAL_ATTACK_FEEDBACK() {
        return  3;
    }

    public int getSPECIAL_ATTACK_DURATION() {
        return 500;
    }

    public int getSPECIAL_ATTACK_COOLDOWN() {
        return 1000;
    }

    public int getSPECIAL_ATTACK_PROPABILITY() {
        return 1;
    }

    public double getGENERIC_MOVEMENT_SPEED() {
        return 0.4;
    }

    public double getGENERIC_ATTACK_DAMADGE() {
        return 40;
    }

    public double getMAXIMUM_HEALTH() {
        return 200;
    }

    public ItemStack getHeadStack() {
        ItemStack stack = new ItemStack(Material.STICK);
        ItemMeta meta = stack.getItemMeta();
        meta.setCustomModelData(6);
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack getDrop() {
        return new ItemStack(Material.NAUTILUS_SHELL);
    }

    public String getName() {
        return "krakenMonster";
    }

    public Hitbox getHitbox() {
        return new Hitbox(drowned.getLocation().subtract(new Vector(0.25, -0.4, 0.25)), 0.5, 0.9, 0.5);
    }
}

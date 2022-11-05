package me.karl.lochness.entities.watermonsters.turtle;

import me.karl.lochness.PluginUtils;
import me.karl.lochness.entities.Hitbox;
import me.karl.lochness.entities.watermonsters.WaterMonster;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Random;

public class LochnessTurtle extends WaterMonster {

    public static final Location barLocation = new Location(Bukkit.getWorlds().get(2), 629, 51, 150);

    public LochnessTurtle(LochnessTurtle turtle) {
        super(turtle);
    }

    public LochnessTurtle(Location loc) {
        super(loc);
    }

    @Override
    protected void attackLogic() {
        super.attackLogic();
        SPECIAL_ATTACK:
        {
            timeSinceLastSpecialAttack++;

            if (timeSinceLastSpecialAttack < SPECIAL_ATTACK_FEEDBACK) {
                drowned.getEquipment().setHelmet(getHeadStack());
            } else if (timeSinceLastSpecialAttack < SPECIAL_ATTACK_FEEDBACK + SPECIAL_ATTACK_DURATION) {
                ItemStack stack = new ItemStack(Material.STICK);
                ItemMeta meta = stack.getItemMeta();
                meta.setCustomModelData(11);
                stack.setItemMeta(meta);
                drowned.getEquipment().setHelmet(stack);
                drowned.setVelocity(new Vector(0, 0, 0));
            } else
                drowned.getEquipment().setHelmet(getHeadStack());

            if (timeSinceLastSpecialAttack < SPECIAL_ATTACK_COOLDOWN)
                break SPECIAL_ATTACK;

            if (new Random().nextInt(SPECIAL_ATTACK_PROPABILITY) != 0)
                break SPECIAL_ATTACK;

            timeSinceLastSpecialAttack = 0;
        }
    }

    @Override
    public void damadge(double damadge) {
        if(timeSinceLastSpecialAttack < SPECIAL_ATTACK_DURATION) {
            for(Entity e: drowned.getNearbyEntities(5, 5, 5)) {
                if(e instanceof Player)
                    ((Player)e).playSound(e.getLocation(), Sound.ENTITY_ARMOR_STAND_HIT, 1, 1);
            }
            super.damadge(damadge / 2);
        }
        else {
            for(Entity e: drowned.getNearbyEntities(5, 5, 5)) {
                if(e instanceof Player)
                    ((Player)e).playSound(e.getLocation(), Sound.ENTITY_TURTLE_HURT, 1, 1);
            }
            super.damadge(damadge);
        }
    }

    @Override
    public Location getBarLocation() {
        return barLocation;
    }

    public int getHIT_COOLDOWN() {
        return 20;
    }

    public int getSPECIAL_ATTACK_FEEDBACK() {
        return  0;
    }

    public int getSPECIAL_ATTACK_DURATION() {
        return 200;
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
        return 90;
    }

    public double getMAXIMUM_HEALTH() {
        return 1500;
    }

    public ItemStack getHeadStack() {
        ItemStack stack = new ItemStack(Material.STICK);
        ItemMeta meta = stack.getItemMeta();
        meta.setCustomModelData(2);
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack getDrop() {
        return new ItemStack(Material.TURTLE_HELMET);
    }

    public String getName() {
        return "turtleMonster";
    }

    public Hitbox getHitbox() {
        return new Hitbox(drowned.getLocation().subtract(new Vector(0.75, -0.75, 0.75)), 1.5, 0.5, 1.5);
    }
}

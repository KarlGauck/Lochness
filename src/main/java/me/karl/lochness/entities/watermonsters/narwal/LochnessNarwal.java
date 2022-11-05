package me.karl.lochness.entities.watermonsters.narwal;

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

public class LochnessNarwal extends WaterMonster {

    public static final Location barLocation = new Location(Bukkit.getWorlds().get(2), 644, 52, 147);

    public LochnessNarwal(LochnessNarwal piranha) {
        super(piranha);
    }

    public LochnessNarwal(Location loc) {
        super(loc);
    }

    private transient Vector knockbackAttackDirection;

    @Override
    protected void attackLogic() {
        super.attackLogic();

        KNOCKBACK_ATTACK:
        {
            timeSinceLastSpecialAttack++;

            if (drowned.getTarget() == null)
                break KNOCKBACK_ATTACK;
            if (!(drowned.getTarget() instanceof Player))
                break KNOCKBACK_ATTACK;
            Player player = Bukkit.getPlayer(drowned.getTarget().getUniqueId());
            if (!isTargetValid(player, drowned.getWorld()))
                break KNOCKBACK_ATTACK;

            if (timeSinceLastSpecialAttack < SPECIAL_ATTACK_FEEDBACK) {
                knockbackAttackDirection = drowned.getEyeLocation().getDirection();
            } else if (timeSinceLastSpecialAttack < SPECIAL_ATTACK_FEEDBACK + SPECIAL_ATTACK_DURATION) {
                drowned.setVelocity(knockbackAttackDirection.normalize().multiply(0.6));
                if (drowned.getEyeLocation().distance(player.getEyeLocation()) < 1) {
                    PluginUtils.damadgePlayer(player, GENERIC_ATTACK_DAMADGE * 1.2, "WMONSTER_1");
                    player.setVelocity(player.getVelocity().add(knockbackAttackDirection.normalize().multiply(2)));
                    timeSinceLastSpecialAttack = SPECIAL_ATTACK_FEEDBACK + SPECIAL_ATTACK_DURATION;
                }
            }

            if (timeSinceLastSpecialAttack < SPECIAL_ATTACK_COOLDOWN)
                break KNOCKBACK_ATTACK;

            if (new Random().nextInt(SPECIAL_ATTACK_PROPABILITY) != 0)
                break KNOCKBACK_ATTACK;

            if (player.getEyeLocation().distance(drowned.getEyeLocation()) < 10) {
                timeSinceLastSpecialAttack = 0;
            }
        }

    }

    @Override
    public void damadge(double damadge) {
        super.damadge(damadge);
        for(Entity e: drowned.getNearbyEntities(5, 5, 5)) {
            if(e instanceof Player)
                ((Player)e).playSound(e.getLocation(), Sound.ENTITY_GLOW_SQUID_HURT, 1, 1);
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
        return  5;
    }

    public int getSPECIAL_ATTACK_DURATION() {
        return 10;
    }

    public int getSPECIAL_ATTACK_COOLDOWN() {
        return 100;
    }

    public int getSPECIAL_ATTACK_PROPABILITY() {
        return 1;
    }

    public double getGENERIC_MOVEMENT_SPEED() {
        return 0.4;
    }

    public double getGENERIC_ATTACK_DAMADGE() {
        return 65;
    }

    public double getMAXIMUM_HEALTH() {
        return 500;
    }

    public ItemStack getHeadStack() {
        ItemStack stack = new ItemStack(Material.STICK);
        ItemMeta meta = stack.getItemMeta();
        meta.setCustomModelData(5);
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack getDrop() {
        return new ItemStack(Material.END_ROD);
    }

    public String getName() {
        return "narwalMonster";
    }

    public Hitbox getHitbox() {
        return new Hitbox(drowned.getLocation().subtract(new Vector(0.5, -0.5, 0.5)), 1, 1, 1);
    }
}

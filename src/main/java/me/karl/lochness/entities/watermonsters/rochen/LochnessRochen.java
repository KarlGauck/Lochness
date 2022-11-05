package me.karl.lochness.entities.watermonsters.rochen;

import me.karl.lochness.PluginUtils;
import me.karl.lochness.entities.Hitbox;
import me.karl.lochness.entities.watermonsters.WaterMonster;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Random;

public class LochnessRochen extends WaterMonster {

    public static final Location barLocation = new Location(Bukkit.getWorlds().get(2), 563, 41, 189);

    public LochnessRochen(LochnessRochen rochen) {
        super(rochen);
    }

    public LochnessRochen(Location loc) {
        super(loc);
    }

    private static final PotionEffectType[] PotionEffectTypes = {
            PotionEffectType.BLINDNESS,
            PotionEffectType.POISON,
            PotionEffectType.WEAKNESS,
            PotionEffectType.UNLUCK,
            PotionEffectType.HUNGER
    };

    @Override
    protected void attackLogic() {
        timeSinceLastHit ++;
        if(timeSinceLastHit > HIT_COOLDOWN) {

            if (drowned.getTarget() == null)
                return;
            Player player = Bukkit.getPlayer(drowned.getTarget().getUniqueId());
            if (!isTargetValid(player, drowned.getWorld()))
                return;

            if (player.getEyeLocation().distance(drowned.getEyeLocation()) > 1.5)
                return;

            RayTraceResult rayTraceResult = drowned.rayTraceBlocks(4);
            if (rayTraceResult != null)
                if (rayTraceResult.getHitBlock() != null)
                    if (rayTraceResult.getHitPosition().distance(drowned.getEyeLocation().toVector()) < player.getEyeLocation().distance(drowned.getEyeLocation()))
                        return;

            PluginUtils.damadgePlayer(player, GENERIC_ATTACK_DAMADGE, "WMONSTER_2");
            player.addPotionEffect(new PotionEffect(PotionEffectTypes[new Random().nextInt(PotionEffectTypes.length)], 100, 1, true, true));
            timeSinceLastHit = 0;

        }
    }

    @Override
    public void damadge(double damadge) {
        super.damadge(damadge);
        for(Entity e: drowned.getNearbyEntities(5, 5, 5)) {
            if(e instanceof Player)
                ((Player)e).playSound(e.getLocation(), Sound.ENTITY_AXOLOTL_DEATH, 1, 1);
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
        return 0;
    }

    public int getSPECIAL_ATTACK_COOLDOWN() {
        return 0;
    }

    public int getSPECIAL_ATTACK_PROPABILITY() {
        return 0;
    }

    public double getGENERIC_MOVEMENT_SPEED() {
        return 0.4;
    }

    public double getGENERIC_ATTACK_DAMADGE() {
        return 25;
    }

    public double getMAXIMUM_HEALTH() {
        return 150;
    }

    public ItemStack getHeadStack() {
        ItemStack stack = new ItemStack(Material.STICK);
        ItemMeta meta = stack.getItemMeta();
        meta.setCustomModelData(3);
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack getDrop() {
        return new ItemStack(Material.PUFFERFISH);
    }

    public String getName() {
        return "rochenMonster";
    }

    public Hitbox getHitbox() {
        return new Hitbox(drowned.getLocation().clone().subtract(new Vector(0.5, -0.5, 0.5)), 1, 0.5, 1);
    }
}

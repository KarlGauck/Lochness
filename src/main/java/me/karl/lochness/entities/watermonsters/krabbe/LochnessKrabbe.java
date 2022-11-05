package me.karl.lochness.entities.watermonsters.krabbe;

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
import org.bukkit.util.Vector;

public class LochnessKrabbe extends WaterMonster {

    public static final Location barLocation = new Location(Bukkit.getWorlds().get(2), 606, 32, 159);

    public LochnessKrabbe(LochnessKrabbe piranha) {
        super(piranha);
    }

    public LochnessKrabbe(Location loc) {
        super(loc);
    }

    @Override
    protected void movementLogic() {
        drowned.setVelocity(drowned.getVelocity().setY(-GENERIC_MOVEMENT_SPEED));
    }

    @Override
    protected void attackLogic() {
        super.attackLogic();
    }

    @Override
    public void damadge(double damadge) {
        super.damadge(damadge);
        for(Entity e: drowned.getNearbyEntities(5, 5, 5)) {
            if(e instanceof Player)
                ((Player)e).playSound(e.getLocation(), Sound.ENTITY_TURTLE_HURT_BABY, 1, 1);
        }
    }

    @Override
    public Location getBarLocation() {
        return barLocation;
    }

    public int getHIT_COOLDOWN() {
        return 10;
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
        return 100;
    }

    public ItemStack getHeadStack() {
        ItemStack stack = new ItemStack(Material.STICK);
        ItemMeta meta = stack.getItemMeta();
        meta.setCustomModelData(7);
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack getDrop() {
        return new ItemStack(Material.BROWN_DYE);
    }

    public String getName() {
        return "krabbeMonster";
    }

    public Hitbox getHitbox() {
        return new Hitbox(drowned.getLocation().subtract(new Vector(0.25, 0, 0.25)), 0.5, 0.5, 0.5);
    }

}

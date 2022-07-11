package me.karl.lochness.entities.watermonsters.hammerhai;

import me.karl.lochness.entities.Hitbox;
import me.karl.lochness.entities.watermonsters.WaterMonster;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class LochnessHammerhai extends WaterMonster {

    public LochnessHammerhai(LochnessHammerhai piranha) {
        super(piranha);
    }

    public LochnessHammerhai(Location loc) {
        super(loc);
    }

    @Override
    protected void attackLogic() {
        super.attackLogic();
    }

    public Location getBarLocation() {
        return new Location(Bukkit.getWorlds().get(2), 574, 38, 280);
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
        return 150;
    }

    public double getMAXIMUM_HEALTH() {
        return 400;
    }

    public ItemStack getHeadStack() {
        ItemStack stack = new ItemStack(Material.STICK);
        ItemMeta meta = stack.getItemMeta();
        meta.setCustomModelData(9);
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack getDrop() {
        return new ItemStack(Material.GRAY_DYE);
    }

    public String getName() {
        return "hammerhaiMonster";
    }

    public Hitbox getHitbox() {
        return new Hitbox(drowned.getLocation().subtract(new Vector(0.5, -0.5, 0.5)), 1, 1, 1);
    }

}

package me.karl.lochness.entities.watermonsters.orca;

import me.karl.lochness.PluginUtils;
import me.karl.lochness.entities.Hitbox;
import me.karl.lochness.entities.LochnessEntity;
import me.karl.lochness.entities.watermonsters.WaterMonster;
import me.karl.lochness.structures.cave.CaveLogic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class LochnessOrca extends WaterMonster {

    protected transient Dolphin dolphin;
    protected UUID dolphinUUID;
    protected transient ArmorStand armorStand;
    protected UUID armorStandUUID;

    private static boolean isAttacking = false;
    private transient boolean isDrownedActive = true;

    private static final int ATTACK_PROPABILITY = 1000;

    public static final Location barLocation = new Location(Bukkit.getWorlds().get(2), 579, 42, 281);

    public LochnessOrca(LochnessOrca orca) {
        super(orca);
        dolphin = (Dolphin) Bukkit.getEntity(orca.dolphinUUID);
        dolphinUUID = orca.dolphinUUID;
        setDolphin();
        armorStand = (ArmorStand) Bukkit.getEntity(orca.armorStandUUID);
        armorStandUUID = orca.armorStandUUID;
        tagEntities(LochnessEntity.entityValidityTag, LochnessEntity.entityValidityNum);
    }

    public LochnessOrca(Location loc) {
        super(loc);
        dolphin = (Dolphin) loc.getWorld().spawnEntity(loc, EntityType.DOLPHIN);
        setDolphin();
        armorStand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        armorStandUUID = armorStand.getUniqueId();
        armorStand.getEquipment().setHelmet(getArmorStandStack());
        armorStand.setSmall(true);
        armorStand.setInvisible(true);
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.addScoreboardTag(getName());
        tagEntities(LochnessEntity.entityValidityTag, LochnessEntity.entityValidityNum);
    }

    @Override
    protected void movementLogic() {

        try {
            if (drowned.getTarget() == null) {
                for (Entity e : drowned.getNearbyEntities(20, 20, 20)) {
                    if (e instanceof Player) {
                        drowned.setTarget((Player) e);
                        break;
                    }
                }
            }

            if (drowned.getTarget() != null && (drowned.getTarget() instanceof Player) && isTargetValid((Player) drowned.getTarget(), drowned.getWorld())) {
                if (isDrownedActive) {
                    if (drowned.getTarget().getLocation().distance(drowned.getLocation()) < 5)
                        isDrownedActive = false;
                    drowned.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2);
                } else {
                    if (drowned.getTarget().getLocation().distance(drowned.getLocation()) > 20)
                        isDrownedActive = true;
                }
            } else {
                isDrownedActive = false;
            }

            if(new Location(Bukkit.getWorlds().get(2), 579, 42, 270).getBlock().getType() == Material.CHISELED_STONE_BRICKS) {
                drowned.teleport(CaveLogic.defense2);
                dolphin.teleport(CaveLogic.defense2);
            }

            if (isAttacking) {
                isDrownedActive = true;
                drowned.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.6);
            }

            if (isDrownedActive) {
                dolphin.teleport(drowned.getLocation().setDirection(drowned.getVelocity()));
            } else {
                drowned.teleport(dolphin.getLocation().setDirection(dolphin.getVelocity()));
            }

            armorStand.teleport(drowned.getLocation().clone().add(armorStand.getLocation().clone().subtract(drowned.getLocation()).toVector().normalize().multiply(3))
                    .setDirection(drowned.getLocation().clone().subtract(armorStand.getLocation()).toVector().normalize()));
            armorStand.setHeadPose(PluginUtils.convertVectorToEulerAngle(armorStand.getLocation(), drowned.getLocation()));
        } catch (Exception e) { }
    }

    @Override
    protected void attackLogic() {
        if(drowned.getTarget() != null && (drowned.getTarget() instanceof Player) && isTargetValid((Player)drowned.getTarget(), drowned.getWorld()))
            if(new Random().nextInt(ATTACK_PROPABILITY) == 0)
                isAttacking = true;
            else;
        else {
            isAttacking = false;
        }

        if(isAttacking)
            super.attackLogic();
    }

    private void setDolphin() {
        if (dolphin != null) {
            dolphin.setCollidable(false);
            dolphin.setInvisible(true);
            dolphin.setInvulnerable(true);
            dolphin.setSilent(true);
            dolphin.setRemoveWhenFarAway(false);
            dolphin.addScoreboardTag(getName());
            dolphinUUID = dolphin.getUniqueId();
        }
    }

    public void damadge(double damadge) {
        super.damadge(damadge);
        isAttacking = true;

        for(Entity e: drowned.getNearbyEntities(5, 5, 5)) {
            if(e instanceof Player)
                ((Player)e).playSound(e.getLocation(), Sound.ENTITY_SQUID_DEATH, 1, 0.5f);
        }
    }

    protected void checkIfDead() {
        super.checkIfDead();
        if (armorStand == null)
            return;
    }

    @Override
    protected void tagEntities(String validityTag, long num) {
        super.tagEntities(validityTag, num);
        LochnessEntity.tagEntity(dolphin, validityTag, num);
        LochnessEntity.tagEntity(armorStand, validityTag, num);
    }

    @Override
    protected void loadEntityByUIDs() {
        super.loadEntityByUIDs();
        dolphin = dolphinUUID == null ? dolphin : (Dolphin) (Bukkit.getEntity(dolphinUUID) == null ? dolphin : Bukkit.getEntity(dolphinUUID));
        armorStand = (ArmorStand) (Bukkit.getEntity(armorStandUUID) == null ? armorStand : Bukkit.getEntity(armorStandUUID));
    }

    @Override
    public ArrayList<UUID> getEntityUUIDs() {
        ArrayList<UUID> list = super.getEntityUUIDs();
        list.add(armorStandUUID);
        list.add(dolphinUUID);
        return list;
    }

    @Override
    public void remove() {
        isAttacking = false;
        if (armorStand != null) {
            armorStand.addScoreboardTag("dead" + getName());
            armorStand.setHealth(0);
            armorStand.remove();
        }
        if (dolphin != null) {
            dolphin.addScoreboardTag("dead" + getName());
            dolphin.setHealth(0);
            dolphin.remove();
        }

        super.remove();
    }

    @Override
    public void save() {
        super.save();
        if (drowned != null) {
            if (dolphin != null)
                dolphin.teleport(drowned);
            if (armorStand != null)
                armorStand.teleport(drowned);
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
        return 90;
    }

    public double getMAXIMUM_HEALTH() {
        return 300;
    }

    public ItemStack getHeadStack() {
        return new ItemStack(Material.AIR);
    }

    public ItemStack getDrop() {
        return new ItemStack(Material.BLACK_DYE);
    }

    public String getName() {
        return "orcaMonster";
    }

    public Hitbox getHitbox() {
        return new Hitbox(drowned.getLocation().clone().add(armorStand.getLocation().clone().subtract(drowned.getLocation()).toVector().normalize().multiply(1.5).subtract(new Vector(
                0.75, -0.25, 0.75))), 1.5, 0.5, 1.5);
    }

    public ItemStack getArmorStandStack() {
        ItemStack stack = new ItemStack(Material.STICK);
        ItemMeta meta = stack.getItemMeta();
        meta.setCustomModelData(4);
        stack.setItemMeta(meta);
        return stack;
    }
}

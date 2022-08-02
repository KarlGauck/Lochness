package me.karl.lochness.entities.watermonsters;

import me.karl.lochness.Lochness;
import me.karl.lochness.PluginUtils;
import me.karl.lochness.commandexecutor.LochnessCommand;
import me.karl.lochness.entities.Hitbox;
import me.karl.lochness.entities.LochnessEntity;
import me.karl.lochness.structures.cave.CaveLogic;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

import java.util.UUID;

public class WaterMonster extends LochnessEntity {

    public transient Drowned drowned;
    protected UUID drownedUUID;

    protected transient ItemStack headStack;

    protected transient Hitbox hitbox;
    protected transient double MAXIMUM_HEALTH = 1;
    protected double health = MAXIMUM_HEALTH;
    protected transient double GENERIC_MOVEMENT_SPEED = 0.2;
    protected transient double GENERIC_ATTACK_DAMADGE = 30;

    protected transient int SPECIAL_ATTACK_FEEDBACK = 0;
    protected transient int SPECIAL_ATTACK_DURATION = 0;
    protected transient int SPECIAL_ATTACK_COOLDOWN = 0;
    protected transient int SPECIAL_ATTACK_PROPABILITY = 0;
    protected int timeSinceLastSpecialAttack = 0;

    protected transient int HIT_COOLDOWN = 20;
    protected int timeSinceLastHit = 0;

    transient Location lastLoc = null;
    transient int falseMovements = 0;

    protected int timeLived = 0;

    public WaterMonster(WaterMonster waterMonster) {
        super(waterMonster.world, waterMonster.chunkX, waterMonster.chunkZ);
        initValues();
        health = waterMonster.health;
        drowned = (Drowned) Bukkit.getEntity(waterMonster.drownedUUID);

        assert drowned != null;
        resetDrowned(drowned.getLocation());
        timeSinceLastSpecialAttack = waterMonster.timeSinceLastSpecialAttack;
        timeSinceLastHit = waterMonster.timeSinceLastHit;
    }

    public WaterMonster(Location loc) {
        super(Bukkit.getWorlds().indexOf(loc.getWorld()), loc.getChunk().getX(), loc.getChunk().getZ());
        initValues();
        health = MAXIMUM_HEALTH;
        spawnDrowned(loc);
    }

    public Runnable tick() {
        return () -> {

            drowned = (Drowned) Bukkit.getEntity(drownedUUID);

            // Only calculate if player is near
            Boolean nearbyPlayer = false;
            for(Entity entity: drowned.getNearbyEntities(80.0, 80.0, 80.0)) {
                if (entity.getType() == EntityType.PLAYER)
                    nearbyPlayer = true;
            }
            if (!nearbyPlayer)
                return;

            timeLived++;

            // ------ Kill old drowneds ------
            for (Entity entity : drowned.getWorld().getEntities()) {
                if (entity.getScoreboardTags().contains("dead")) {
                    entity.remove();
                    if (entity instanceof LivingEntity)
                        ((LivingEntity) entity).setHealth(0.0);
                }
            }

            // ------ if dead ------
            checkIfDead();

            // Fix movementbug
            boolean isAttacking = timeSinceLastSpecialAttack < SPECIAL_ATTACK_FEEDBACK + SPECIAL_ATTACK_DURATION;

            if((!isAttacking) && lastLoc != null) {
                if (lastLoc.toVector().distance(drowned.getLocation().toVector()) < 0.05) {
                    falseMovements++;
                } else {
                    falseMovements = 0;
                }
            }
            lastLoc = drowned.getLocation();

            if(falseMovements > 40) {
                // Bukkit.broadcastMessage("testmessage: reset drowned falseMovement");
                // resetDrowned(drowned.getLocation());
                falseMovements = 0;
            }

            // ------ Potion Effects and stuff ------
            if (timeLived % 10000 == 0) {
                resetDrowned(drowned.getLocation());
            }

            hitbox = getHitbox();

            if(hitbox != null && LochnessCommand.showHitbox)
                hitbox.display();

            calculateProjectiles();
            movementLogic();
            if (drowned.isSwimming())
                attackLogic();

        };
    }

    @Override
    public void updateGlobalChunkPos() {
        if (!drowned.getLocation().getChunk().isLoaded())
            return;
        chunkX = drowned.getLocation().getChunk().getX();
        chunkZ = drowned.getLocation().getChunk().getZ();
        world = Bukkit.getWorlds().indexOf(drowned.getWorld());
    }

    protected void movementLogic() {

    }

    protected void attackLogic() {
        timeSinceLastHit++;
        if (timeSinceLastHit > HIT_COOLDOWN) {

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

            PluginUtils.damadgePlayer(player, GENERIC_ATTACK_DAMADGE, "WMONSTER_3");
            timeSinceLastHit = 0;

        }
    }

    public void damadge(double damadge) {
        health = health - damadge;
        if (health < 0) {
            drowned.getWorld().dropItem(drowned.getLocation(), getDrop());
            ExperienceOrb orb = (ExperienceOrb) drowned.getWorld().spawnEntity(drowned.getLocation(), EntityType.EXPERIENCE_ORB);
            orb.setExperience(100);
            ArmorStand armorStand = (ArmorStand) drowned.getWorld().spawnEntity(drowned.getLocation(), EntityType.ARMOR_STAND);
            armorStand.setSmall(true);
            armorStand.setSilent(true);
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.teleport(drowned.getLocation());
            armorStand.getEquipment().setItemInMainHand(getHeadStack());
            remove();
        }
        if (health > MAXIMUM_HEALTH)
            health = MAXIMUM_HEALTH;
    }

    private void spawnDrowned(Location loc) {
        drowned = (Drowned) loc.getWorld().spawnEntity(loc, EntityType.DROWNED);
        drowned.setInvisible(true);
        drowned.getEquipment().setHelmet(headStack);
        drowned.teleport(loc);
        drowned.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 42, 1, false, false));
        drowned.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 255, false, false));
        drowned.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 999999, 255, false, false));
        drowned.setRemoveWhenFarAway(false);
        drowned.setPersistent(true);
        drowned.leaveVehicle();
        drowned.setBaby();
        drowned.setSilent(true);
        drowned.setCollidable(false);
        ItemStack hand = new ItemStack(Material.COMMAND_BLOCK_MINECART);
        drowned.getEquipment().setItemInMainHand(hand);
        drowned.addScoreboardTag(getName());
        drownedUUID = drowned.getUniqueId();
        drowned.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(100);
        drowned.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(GENERIC_MOVEMENT_SPEED);
        drowned.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(GENERIC_ATTACK_DAMADGE);
        drowned.addScoreboardTag("waterMonster");
    }

    private void resetDrowned(Location loc) {
        if(drowned != null) {
            drowned.addScoreboardTag("dead" + getName());
            drowned.setLootTable(LootTables.SILVERFISH.getLootTable());
            drowned.remove();
            drowned.setHealth(0);
        }
        spawnDrowned(loc);
    }

    public void calculateProjectiles() {
        ENTITY:
        for (Entity entity : drowned.getWorld().getEntities()) {

            if (!(entity instanceof Trident))
                continue;

            if (entity.getScoreboardTags().contains("watermonsterHitProjectile"))
                continue;

            Location loc = entity.getLocation().clone();
            loc.setDirection(loc.getDirection().multiply(-1));
            Location hitLoc = hitbox.getIntersectionPoint(loc);
            if (hitLoc == null)
                continue;

            if (hitLoc.distance(loc) < 1) {
                entity.addScoreboardTag("watermonsterHitProjectile");
                damadge(((Trident) entity).getDamage());
                continue ENTITY;
            }
        }
    }

    protected void checkIfDead() {
        if (drowned.getHealth() <= 0.0) {
            remove();
        }
    }

    public void remove() {
        drowned.addScoreboardTag("dead" + getName());
        drowned.setLootTable(LootTables.SILVERFISH.getLootTable());
        drowned.remove();
        drowned.setHealth(0);
        stop();
        if (!LochnessEntity.isEntityAlive(this.getClass()) && !Lochness.shutdown && !Lochness.restart && !CaveLogic.isResetingCave) {
            this.getBarLocation().getBlock().setType(Material.WATER);
        }
    }

    @Override
    protected void save() {
        world = Bukkit.getWorlds().indexOf(drowned.getWorld());
        chunkX = drowned.getLocation().getChunk().getX();
        chunkZ = drowned.getLocation().getChunk().getZ();
    }

    public void initValues() {
        MAXIMUM_HEALTH = getMAXIMUM_HEALTH();
        GENERIC_MOVEMENT_SPEED = getGENERIC_MOVEMENT_SPEED();
        GENERIC_ATTACK_DAMADGE = getGENERIC_ATTACK_DAMADGE();
        SPECIAL_ATTACK_FEEDBACK = getSPECIAL_ATTACK_FEEDBACK();
        SPECIAL_ATTACK_PROPABILITY = getSPECIAL_ATTACK_PROPABILITY();
        SPECIAL_ATTACK_DURATION = getSPECIAL_ATTACK_DURATION();
        SPECIAL_ATTACK_COOLDOWN = getSPECIAL_ATTACK_COOLDOWN();
        timeSinceLastSpecialAttack = SPECIAL_ATTACK_COOLDOWN;
        HIT_COOLDOWN = getHIT_COOLDOWN();
        headStack = getHeadStack();
    }

    public Location getLocation() {
        return drowned.getLocation().clone();
    }

    public boolean isLoaded() {
        return drowned.getLocation().getChunk().isLoaded();
    }

    public Location getBarLocation() {
        return null;
    }

    public Hitbox getHitbox() {
        return hitbox;
    }

    public int getHIT_COOLDOWN() {
        return 20;
    }

    public int getSPECIAL_ATTACK_FEEDBACK() {
        return 0;
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
        return 0;
    }

    public double getGENERIC_ATTACK_DAMADGE() {
        return 0;
    }

    public double getMAXIMUM_HEALTH() {
        return 0;
    }

    public ItemStack getHeadStack() {
        return null;
    }

    public ItemStack getDrop() {
        return null;
    }

    public String getName() {
        return null;
    }
}

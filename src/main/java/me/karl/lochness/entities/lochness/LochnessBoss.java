package me.karl.lochness.entities.lochness;

import me.karl.lochness.Lochness;
import me.karl.lochness.PluginUtils;
import me.karl.lochness.commandexecutor.Debug;
import me.karl.lochness.entities.Hitbox;
import me.karl.lochness.entities.LochnessEntity;
import me.karl.lochness.structures.cave.BlockBreakEvent;
import me.karl.lochness.structures.cave.InteractionEvent;
import me.karl.lochness.structures.endcredits.MessageHandler;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class LochnessBoss extends LochnessEntity implements Serializable {
    public static final long serialVersionUID = 1;

    public boolean visible = false;

    public transient BossBar bossBar;

    double bodyPartDistance = 1.5;

    transient Drowned drowned;
    transient Dolphin dolphin;

    transient ArmorStand head, neck;
    transient ArmorStand[] body = new ArmorStand[8];
    transient ArmorStand[] tail = new ArmorStand[2];

    public UUID drownedUUID, dolphinUUID, headUUID, neckUUID;
    UUID[] bodyUUID = new UUID[body.length];
    UUID[] tailUUID = new UUID[tail.length];

    public transient Hitbox headHitbox, neckHitbox;
    public transient Hitbox[] bodyHitbox = new Hitbox[body.length];
    public transient Hitbox[] tailHitbox = new Hitbox[tail.length];

    transient int lastTimeSwimming = 0;
    transient Location headLoc;

    transient Location lastLoc = null;
    transient int falseMovements = 0;
    transient boolean isAttacking = false;

    // ------------ LOCHNESS INDIVIDUAL ------------
    private static final double MAXIMUM_HEALTH = 10000;
    private double health = MAXIMUM_HEALTH;
    AI_STATE aiState = AI_STATE.ATTACK;
    private long timeLived = 0;
    private int phase = 1;

    // ------------ LOCHNESS POTION EFFECT VARIABLES ------------
    public static double REGENERATION_EFFECT = 0;
    public static double DEFENSE_EFFECT_FACTOR = 1;
    public static double STRENGTH_EFFECT_FACTOR = 1;

    // ------------ Attack variables ------------
    private static final int ATTACK_COOLDOWN = 300;
    private static final int ATTACK_MAXIMUM_PAUSEE = 400;
    private transient int timeSinceLastAttack = 0;

    // ---- fire attack ----
    private transient ArrayList<Location> fireLocation = new ArrayList<>();
    private transient ArrayList<Integer> fireLocationTime = new ArrayList<>();
    private static final int FIRE_ATTACK_DURATION = 100;
    private static final int FIRE_ATTACK_COOLDOWN = 400;
    private static final int FIRE_ATTACK_FEEDBACK = 30;
    private static final double FIRE_ATTACK_DAMADGE = 100;
    private static final int FIRE_ATTACK_PROPABILITY = 300;
    private transient int timeSinceLastFireAttack = FIRE_ATTACK_FEEDBACK + FIRE_ATTACK_DURATION;

    // ---- knockback attack ----
    private transient ArrayList<UUID> knockbackPlayer = new ArrayList<>();
    private transient ArrayList<Integer> knockbackTime = new ArrayList<>();
    private static final int KNOCKBACK_ATTACK_COOLDOWN = 400;
    private static final int KNOCKBACK_ATTACK_FEEDBACK = 15;
    private static final int KNOCKBACK_ATTACK_DURATION = 15;
    private static final double KNOCKBACK_ATTACK_DAMADGE = 400;
    private static final int KNOCKBACK_ATTACK_PROPABILITY = 100;
    private transient int timeSinceLastKnockbackAttack = KNOCKBACK_ATTACK_FEEDBACK + KNOCKBACK_ATTACK_DURATION;
    private transient Vector knockbackAttackDirection;

    // ---- freeze attack ----
    private static final int FREEZE_ATTACK_COOLDOWN = 800;
    private static final int FREEZE_ATTACK_FEEDBACK = 50;
    private static final int FREEZE_ATTACK_DURATION = 300;
    private static final double FREEZE_ATTACK_DAMADGE = 5;
    private static final int FREEZE_ATTACK_PROPABILITY = 100;
    private transient int timeSinceLastFreezeAttack = FREEZE_ATTACK_FEEDBACK + FREEZE_ATTACK_DURATION;
    private transient Location freezeLoc;

    // ---- catch attack ----
    private static final int CATCH_ATTACK_COOLDOWN = 600;
    private static final int CATCH_ATTACK_FEEDBACK = 100;
    private static final int CATCH_ATTACK_DURATION = 300;
    private static final int CATCH_ATTACK_DAMADGE = 80;
    private static final int CATCH_ATTACK_PROPABILITY = 100;
    private transient int timeSinceLastCatchAttack = CATCH_ATTACK_FEEDBACK + CATCH_ATTACK_DURATION;

    // ---- poison attack ----
    private static final int POISON_ATTACK_COOLDOWN = 2500;
    private static final int POISON_ATTACK_FEEDBACK = 50;
    private static final int POISON_ATTACK_DURATION = 300;
    private static final double POISON_ATTACK_DAMADGE = 5;
    private static final int POISON_ATTACK_PROPABILITY = 100;
    private transient int timeSinceLastPoisonAttack = POISON_ATTACK_FEEDBACK + POISON_ATTACK_DURATION;

    // ------------ Effect variables ------------
    private static final int INVISIBILITY_EFFECT_DURATION = 350;
    private static final int INVISIBILITY_EFFECT_COOLDOWN = 1000;
    private static final int INVISIBILITY_EFFECT_PROPABILITY = 100;
    private transient int timeSinceLastInvisEffect = INVISIBILITY_EFFECT_DURATION;

    private static final int BLINDNESS_PROPABILITY = 2000;
    private static final int BLINDNESS_DURATION = 200;
    private int count;

    private static final int SPAWN_EFFECT_COOLDOWN = 2500;
    private transient int timeSinceLastSpawn = 1;

    public LochnessBoss(LochnessBoss lochnessBoss) {
        super(lochnessBoss.world, lochnessBoss.chunkX, lochnessBoss.chunkZ);

        bossBar = Bukkit.createBossBar(NamespacedKey.minecraft("1"), "Lochness", BarColor.WHITE, BarStyle.SEGMENTED_12, BarFlag.CREATE_FOG, BarFlag.DARKEN_SKY);
        bossBar.setVisible(true);
        bossBar.setProgress(1);

        drowned = (Drowned) Bukkit.getEntity(lochnessBoss.drownedUUID);
        drowned.setCollidable(false);
        drowned.setPersistent(true);
        drownedUUID = drowned.getUniqueId();

        dolphin = (Dolphin) Bukkit.getEntity(lochnessBoss.dolphinUUID);
        dolphin.setCollidable(false);
        dolphinUUID = dolphin.getUniqueId();

        head = (ArmorStand) Bukkit.getEntity(lochnessBoss.headUUID);
        headUUID = head.getUniqueId();

        neck = (ArmorStand) Bukkit.getEntity(lochnessBoss.neckUUID);
        neckUUID = neck.getUniqueId();

        for (int bodyPart = 0; bodyPart < body.length; bodyPart++) {
            body[bodyPart] = (ArmorStand) Bukkit.getEntity(lochnessBoss.bodyUUID[bodyPart]);
            bodyUUID[bodyPart] = body[bodyPart].getUniqueId();
        }
        for (int tailPart = 0; tailPart < tail.length; tailPart++) {
            tail[tailPart] = (ArmorStand) Bukkit.getEntity(lochnessBoss.tailUUID[tailPart]);
            tailUUID[tailPart] = tail[tailPart].getUniqueId();
        }

        visible = lochnessBoss.visible;
        health = lochnessBoss.health;
        phase = lochnessBoss.phase;
        headLoc = head.getLocation();
        bodyPartDistance = lochnessBoss.bodyPartDistance;
        timeLived = lochnessBoss.timeLived;
    }

    public LochnessBoss(Location loc) {
        super(Bukkit.getWorlds().indexOf(loc.getWorld()), loc.getChunk().getX(), loc.getChunk().getZ());

        bossBar = Bukkit.createBossBar(NamespacedKey.minecraft("1"), "Lochness", BarColor.WHITE, BarStyle.SEGMENTED_12, BarFlag.CREATE_FOG, BarFlag.DARKEN_SKY);
        bossBar.setVisible(true);
        bossBar.setProgress(1);

        World world = loc.getWorld();

        // ------ Set entity Locations ------
        headLoc = loc;

        // ------ Spawn entities ------
        resetDrowned(headLoc);

        dolphin = (Dolphin) world.spawnEntity(headLoc, EntityType.DOLPHIN);
        dolphin.setRemoveWhenFarAway(false);
        dolphin.setPersistent(true);
        dolphin.setInvulnerable(true);
        dolphin.setInvisible(true);
        dolphin.setCollidable(false);
        dolphin.addScoreboardTag("lochnessBoss");
        dolphinUUID = dolphin.getUniqueId();

        head = (ArmorStand) world.spawnEntity(headLoc, EntityType.ARMOR_STAND);
        head.setInvisible(true);
        head.setInvulnerable(true);
        head.setGravity(false);
        head.setSmall(true);
        head.addScoreboardTag("lochnessBoss");
        headUUID = head.getUniqueId();

        neck = (ArmorStand) world.spawnEntity(headLoc.clone().add(new Vector(0, 0, 0.5)), EntityType.ARMOR_STAND);
        neck.setInvisible(true);
        neck.setInvulnerable(true);
        neck.setGravity(false);
        neck.setSmall(true);
        neck.addScoreboardTag("lochnessBoss");
        neckUUID = neck.getUniqueId();

        for (int bodyPart = 0; bodyPart < body.length; bodyPart++) {
            body[bodyPart] = (ArmorStand) world.spawnEntity(headLoc.clone().add(new Vector(0, 0, 1 + bodyPart * 0.5)), EntityType.ARMOR_STAND);
            body[bodyPart].setInvisible(true);
            body[bodyPart].setInvulnerable(true);
            body[bodyPart].setGravity(false);
            body[bodyPart].setSmall(true);
            body[bodyPart].addScoreboardTag("lochnessBoss");
            bodyUUID[bodyPart] = body[bodyPart].getUniqueId();
        }
        for (int tailPart = 0; tailPart < tail.length; tailPart++) {
            tail[tailPart] = (ArmorStand) world.spawnEntity(headLoc.clone().add(new Vector(0, 0, 4 + tailPart * 0.5)), EntityType.ARMOR_STAND);
            tail[tailPart].setInvisible(true);
            tail[tailPart].setInvulnerable(true);
            tail[tailPart].setGravity(false);
            tail[tailPart].setSmall(true);
            tail[tailPart].addScoreboardTag("lochnessBoss");
            tailUUID[tailPart] = tail[tailPart].getUniqueId();
        }

        // ------ Set Models ------
        setVisible(true);

    }

    @Override
    public Runnable tick() {
        return new Runnable() {
            @Override
            public void run() {

                if (!drowned.getLocation().getChunk().isLoaded())
                    return;

                timeLived++;

                // ------ if dead ------
                checkIfDead();

                if (timeLived % 10000 == 0) {
                    resetDrowned(drowned.getLocation());
                }

                // Fix movementbug
                if(lastLoc != null) {
                    if (!(isAttacking) && aiState == AI_STATE.ATTACK && lastLoc.toVector().distance(drowned.getLocation().toVector()) < 0.05) {
                        falseMovements++;
                    } else {
                        falseMovements = 0;
                    }
                }
                lastLoc = drowned.getLocation();

                if(falseMovements > 40) {
                    resetDrowned(drowned.getLocation());
                    falseMovements = 0;
                }

                // ------ Potion Effects and stuff ------

                drowned.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 42, 42, true, false));
                drowned.getEquipment().setItemInMainHand(new ItemStack(Material.COMMAND_BLOCK_MINECART));

                loadHitboxes();
                calculateProjectiles();

                for(Entity entity: drowned.getWorld().getEntities()) {
                    if(entity.getScoreboardTags().contains("deadLochness"))
                        entity.remove();
                }

                // ------ Bossbar ------
                bossBar.setProgress(health / MAXIMUM_HEALTH);
                for(Entity e: drowned.getNearbyEntities(60, 60, 60)) {
                    if(e instanceof Player) {
                        if(e.getLocation().distance(drowned.getLocation()) > 50)
                            bossBar.removePlayer((Player) e);
                        else
                            bossBar.addPlayer((Player)e);
                    }
                }

                // ------ AI ------
                if (lastTimeSwimming > 0)
                    lastTimeSwimming--;
                if (drowned.isSwimming())
                    lastTimeSwimming = 20;

                if (lastTimeSwimming > 0 && InteractionEvent.doorLoc.getBlock().getType() == Material.WATER)
                    aiState = AI_STATE.ATTACK;
                else
                    aiState = AI_STATE.RANDOM_MOVEMENT;

                switch (aiState) {
                    case ATTACK: {
                        head.teleport(drowned);
                        dolphin.teleport(head);
                        attackLogic();
                        effectLogic();
                        break;
                    }
                    case RANDOM_MOVEMENT: {
                        head.teleport(dolphin);
                        drowned.teleport(head);
                        break;
                    }
                }

                // ------ Regeneration ------
                health = health + REGENERATION_EFFECT;
                if (health > MAXIMUM_HEALTH)
                    health = MAXIMUM_HEALTH;

                // ------ Snake body movement ------

                try {
                    neck.teleport(head.getLocation().add(neck.getLocation().subtract(head.getLocation()).toVector().normalize().multiply(1.5)));
                    neck.teleport(neck.getLocation().setDirection(head.getLocation().subtract(neck.getLocation()).toVector()));
                    neck.setHeadPose(PluginUtils.convertVectorToEulerAngle(neck.getLocation(), head.getLocation()));

                    body[0].teleport(neck.getLocation().add(body[0].getLocation().subtract(neck.getLocation()).toVector().normalize().multiply(bodyPartDistance)));
                    body[0].teleport(body[0].getLocation().setDirection(neck.getLocation().subtract(body[0].getLocation()).toVector()));
                    body[0].setHeadPose(PluginUtils.convertVectorToEulerAngle(body[0].getLocation(), neck.getLocation()));

                    for (int bodyPart = 1; bodyPart < body.length; bodyPart++) {
                        body[bodyPart].teleport(body[bodyPart - 1].getLocation().add(body[bodyPart].getLocation().subtract(body[bodyPart - 1].getLocation()).toVector().normalize().multiply(bodyPartDistance)));
                        body[bodyPart].teleport(body[bodyPart].getLocation().setDirection(body[bodyPart - 1].getLocation().subtract(body[bodyPart].getLocation()).toVector()));
                        body[bodyPart].setHeadPose(PluginUtils.convertVectorToEulerAngle(body[bodyPart].getLocation(), body[bodyPart - 1].getLocation()));
                    }
                    tail[0].teleport(body[body.length - 1].getLocation().add(tail[0].getLocation().subtract(body[body.length - 1].getLocation()).toVector().normalize().multiply(bodyPartDistance)));
                    tail[0].teleport(tail[0].getLocation().setDirection(body[body.length - 1].getLocation().subtract(tail[0].getLocation()).toVector()));
                    tail[0].setHeadPose(PluginUtils.convertVectorToEulerAngle(tail[0].getLocation(), body[body.length - 1].getLocation()));
                    for (int tailPart = 1; tailPart < tail.length; tailPart++) {
                        tail[tailPart].teleport(tail[tailPart - 1].getLocation().add(tail[tailPart].getLocation().subtract(tail[tailPart - 1].getLocation()).toVector().normalize().multiply(bodyPartDistance)));
                        tail[tailPart].teleport(tail[tailPart].getLocation().setDirection(tail[tailPart - 1].getLocation().subtract(tail[tailPart].getLocation()).toVector()));
                        tail[tailPart].setHeadPose(PluginUtils.convertVectorToEulerAngle(tail[tailPart].getLocation(), tail[tailPart - 1].getLocation()));
                    }
                } catch (Exception e) {
                }

            }
        };
    }

    private void attackLogic() {

        timeSinceLastAttack++;

        isAttacking = false;

        for(Entity entity: drowned.getNearbyEntities(20, 20, 20)) {
            if(entity instanceof Player)
                ((Player)entity).removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
        }

        MEELE:
        {
            if (drowned.getTarget() == null)
                break MEELE;
            Player player = Bukkit.getPlayer(drowned.getTarget().getUniqueId());
            if (!isTargetValid(player, drowned.getWorld()))
                break MEELE;

            if (player.getEyeLocation().distance(drowned.getEyeLocation()) > 3.5)
                break MEELE;

            RayTraceResult rayTraceResult = drowned.rayTraceBlocks(4);
            if (rayTraceResult != null)
                if (rayTraceResult.getHitBlock() != null)
                    if (rayTraceResult.getHitPosition().distance(drowned.getEyeLocation().toVector()) < player.getEyeLocation().distance(drowned.getEyeLocation()))
                        break MEELE;

            if (new Random().nextInt(20) == 0) {
                PluginUtils.damadgePlayer(player, 50 * STRENGTH_EFFECT_FACTOR, "LOCHNESS_2");
            }
        }

        FIRE_ATTACK:
        {
            timeSinceLastFireAttack++;

            if (timeSinceLastFireAttack < FIRE_ATTACK_FEEDBACK) {
                drowned.setVelocity(drowned.getEyeLocation().getDirection().normalize().multiply(0.01));
                isAttacking = true;
                Location l = drowned.getEyeLocation().clone().subtract(drowned.getEyeLocation().getDirection().normalize().multiply(0.3));
                l.getWorld().spawnParticle(Particle.SOUL, l, 10, 0.1, 0.1, 0.1, 0.03);
            } else if (timeSinceLastFireAttack < FIRE_ATTACK_DURATION + FIRE_ATTACK_FEEDBACK) {
                fireLocation.add(drowned.getEyeLocation().clone().add(drowned.getEyeLocation().getDirection()));
                fireLocationTime.add(0);
                drowned.setVelocity(drowned.getEyeLocation().getDirection().normalize().multiply(0.01));
                isAttacking = true;
            }

            if (timeSinceLastFireAttack < FIRE_ATTACK_COOLDOWN || timeSinceLastAttack < ATTACK_COOLDOWN)
                break FIRE_ATTACK;

            if (drowned.getTarget() == null)
                break FIRE_ATTACK;
            Player player = Bukkit.getPlayer(drowned.getTarget().getUniqueId());
            if (!isTargetValid(player, drowned.getWorld()))
                break FIRE_ATTACK;

            if (new Random().nextInt(FIRE_ATTACK_PROPABILITY) != 0)
                break FIRE_ATTACK;

            if (player.getLocation().distance(drowned.getEyeLocation()) < 12) {
                timeSinceLastFireAttack = 0;
                timeSinceLastAttack = 0;
            }
        }

        FIRE_ATTACK_ENTITIES:
        {
            for (int fireLocationIndex = fireLocation.size() - 1; fireLocationIndex >= 0; fireLocationIndex--) {
                fireLocationTime.set(fireLocationIndex, fireLocationTime.get(fireLocationIndex) + 1);
                if (fireLocationTime.get(fireLocationIndex) > 25) {
                    fireLocationTime.remove(fireLocationIndex);
                    fireLocation.remove(fireLocationIndex);
                    continue;
                }
                fireLocation.set(fireLocationIndex, fireLocation.get(fireLocationIndex).add(fireLocation.get(fireLocationIndex).getDirection().normalize().multiply(0.7)));
                Location l = fireLocation.get(fireLocationIndex).clone();
                for (Player p : l.getWorld().getPlayers()) {
                    if (p.getLocation().distance(l.subtract(new Vector(0, 1, 0))) < 2)
                        PluginUtils.damadgePlayer(p, FIRE_ATTACK_DAMADGE * STRENGTH_EFFECT_FACTOR, "LOCHNESS_3");
                }
                l.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, l, 30, 0.6, 0.2, 0.6, 0);
            }
        }

        KNOCKBACK_ATTACK:
        {
            timeSinceLastKnockbackAttack++;

            if (drowned.getTarget() == null)
                break KNOCKBACK_ATTACK;
            if (!(drowned.getTarget() instanceof Player))
                break KNOCKBACK_ATTACK;
            Player player = Bukkit.getPlayer(drowned.getTarget().getUniqueId());
            if (!isTargetValid(player, drowned.getWorld()))
                break KNOCKBACK_ATTACK;

            if (timeSinceLastKnockbackAttack < KNOCKBACK_ATTACK_FEEDBACK) {
                drowned.setVelocity(drowned.getEyeLocation().getDirection().multiply(0.03));
                isAttacking = true;
                Location l = drowned.getEyeLocation().clone().subtract(drowned.getEyeLocation().getDirection().normalize().multiply(0.3));
                l.getWorld().spawnParticle(Particle.CRIT, l, 10, 0.3, 0.3, 0.3, 0.03);
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10, 0, false, false));
                knockbackAttackDirection = drowned.getEyeLocation().getDirection();
            } else if (timeSinceLastKnockbackAttack < KNOCKBACK_ATTACK_FEEDBACK + KNOCKBACK_ATTACK_DURATION) {
                drowned.setVelocity(knockbackAttackDirection.normalize().multiply(1));
                if (drowned.getEyeLocation().distance(player.getEyeLocation()) < 1) {
                    PluginUtils.damadgePlayer(player, KNOCKBACK_ATTACK_DAMADGE * STRENGTH_EFFECT_FACTOR, "LOCHNESS_4");
                    player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 200, false, false));
                    knockbackPlayer.add(player.getUniqueId());
                    knockbackTime.add(0);
                    timeSinceLastKnockbackAttack = KNOCKBACK_ATTACK_FEEDBACK + KNOCKBACK_ATTACK_DURATION;
                }
            }

            if (timeSinceLastKnockbackAttack < KNOCKBACK_ATTACK_COOLDOWN || timeSinceLastAttack < ATTACK_COOLDOWN)
                break KNOCKBACK_ATTACK;

            if (new Random().nextInt(KNOCKBACK_ATTACK_PROPABILITY) != 0)
                break KNOCKBACK_ATTACK;

            if (player.getEyeLocation().distance(drowned.getEyeLocation()) < 10) {
                timeSinceLastKnockbackAttack = 0;
                timeSinceLastAttack = 0;
            }
        }

        KNOCKBACK_ATTACK_KNOCKBACK:
        {
            for (int playerIndex = knockbackPlayer.size() - 1; playerIndex >= 0; playerIndex--) {
                Player player = Bukkit.getPlayer(knockbackPlayer.get(playerIndex));
                if (!isTargetValid(player, drowned.getWorld()) || knockbackTime.get(playerIndex) > 5) {
                    knockbackPlayer.remove(playerIndex);
                    knockbackTime.remove(playerIndex);
                    continue;
                }
                player.setVelocity(knockbackAttackDirection.multiply(1.3));
                knockbackTime.set(playerIndex, knockbackTime.get(playerIndex) + 1);
            }
        }

        CATCH_ATTACK:
        {
            if(phase < 4)
                break CATCH_ATTACK;

            timeSinceLastCatchAttack++;

            if (drowned.getTarget() == null)
                break CATCH_ATTACK;
            if (!(drowned.getTarget() instanceof Player))
                break CATCH_ATTACK;
            Player player = Bukkit.getPlayer(drowned.getTarget().getUniqueId());
            if (!isTargetValid(player, drowned.getWorld()))
                break CATCH_ATTACK;

            if (timeSinceLastCatchAttack < CATCH_ATTACK_FEEDBACK) {
                Location l = drowned.getEyeLocation().clone().subtract(drowned.getEyeLocation().getDirection().normalize().multiply(0.3));
                l.getWorld().spawnParticle(Particle.CLOUD, l, count, 0.3, 0.3, 0.3, 0.03);
            } else if (timeSinceLastCatchAttack < CATCH_ATTACK_FEEDBACK + CATCH_ATTACK_DURATION) {
                drowned.setVelocity(new Vector(0, 0, 0));
                isAttacking = true;
                drowned.getTarget().teleport(drowned.getLocation().clone().add(drowned.getLocation().getDirection().multiply(0.4)).setDirection(drowned.getTarget().getLocation().getDirection()));
                PluginUtils.damadgePlayer((Player) drowned.getTarget(), CATCH_ATTACK_DAMADGE * STRENGTH_EFFECT_FACTOR, "LOCHNESS_5");
            }

            if (timeSinceLastCatchAttack < CATCH_ATTACK_COOLDOWN || timeSinceLastAttack < ATTACK_COOLDOWN)
                break CATCH_ATTACK;

            if (new Random().nextInt(CATCH_ATTACK_PROPABILITY) != 0)
                break CATCH_ATTACK;

            if(drowned.getLocation().distance(drowned.getTarget().getLocation()) > 6) {
                break CATCH_ATTACK;
            }

            timeSinceLastCatchAttack = 0;
            timeSinceLastAttack = 0;
        }

        FREEZE_ATTACK:
        {
            timeSinceLastFreezeAttack++;

            if (drowned.getTarget() == null)
                break FREEZE_ATTACK;
            Player player = Bukkit.getPlayer(drowned.getTarget().getUniqueId());
            if (!isTargetValid(player, drowned.getWorld()))
                break FREEZE_ATTACK;

            if (timeSinceLastFreezeAttack < FREEZE_ATTACK_FEEDBACK) {
                freezeLoc = player.getLocation();
            } else if (timeSinceLastFreezeAttack == FREEZE_ATTACK_FEEDBACK) {
                player.teleport(freezeLoc.getBlock().getLocation().add(new Vector(0.5, 0, 0.5)).setDirection(freezeLoc.getDirection()));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + player.getUniqueId() + " at @s run fill ~-1 ~-1 ~-1 ~1 ~2 ~1 ice replace water");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + player.getUniqueId() + " at @s run fill ~-1 ~-1 ~-1 ~1 ~2 ~1 ice replace seagrass");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + player.getUniqueId() + " at @s run fill ~-1 ~-1 ~-1 ~1 ~2 ~1 ice replace tall_seagrass");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + player.getUniqueId() + " at @s run fill ~ ~ ~ ~ ~1 ~ water replace ice");

                Bukkit.getScheduler().runTaskLater(Lochness.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + player.getUniqueId() + " at @s run fill ~-2 ~-2 ~-2 ~2 ~3 ~2 water replace ice");
                    }
                }, FREEZE_ATTACK_DURATION);

            } else if (timeSinceLastFreezeAttack < FREEZE_ATTACK_FEEDBACK + FREEZE_ATTACK_DURATION) {
                //during attack
            } else if (timeSinceLastFreezeAttack == FREEZE_ATTACK_FEEDBACK + FREEZE_ATTACK_DURATION) {
                //end of attack
            }

            if (timeSinceLastFreezeAttack < FREEZE_ATTACK_COOLDOWN || timeSinceLastAttack < ATTACK_COOLDOWN)
                break FREEZE_ATTACK;

            if (new Random().nextInt(FREEZE_ATTACK_PROPABILITY) != 0)
                break FREEZE_ATTACK;

            timeSinceLastAttack = 0;
            timeSinceLastFreezeAttack = 0;
        }

        POISON_ATTACK:
        {

            if(phase < 2)
                break POISON_ATTACK;

            timeSinceLastPoisonAttack++;

            if (drowned.getTarget() == null)
                break POISON_ATTACK;
            if (!(drowned.getTarget() instanceof Player))
                break POISON_ATTACK;
            Player player = Bukkit.getPlayer(drowned.getTarget().getUniqueId());
            if (!isTargetValid(player, drowned.getWorld()))
                break POISON_ATTACK;

            if (timeSinceLastPoisonAttack < POISON_ATTACK_FEEDBACK) {
                Location l = drowned.getEyeLocation().clone().subtract(drowned.getEyeLocation().getDirection().normalize().multiply(0.3));
                l.getWorld().spawnParticle(Particle.SNEEZE, l, count, 0.3, 0.3, 0.3, 0.03);
            } else if (timeSinceLastPoisonAttack < POISON_ATTACK_FEEDBACK + POISON_ATTACK_DURATION) {
                displayParticles(Particle.SNEEZE, 10, 4, 4, 4, 0, null);

                // give poison effect
                for(Player p: drowned.getWorld().getPlayers()) {
                    for(ArmorStand a: tail) {
                        if(p.getLocation().toVector().distance(a.getLocation().toVector()) < 4)
                            p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 800, 5, true, true));
                    }
                    for(ArmorStand a: body) {
                        if(p.getLocation().toVector().distance(a.getLocation().toVector()) < 4)
                            p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 800, 5, true, true));
                    }
                    if(p.getLocation().toVector().distance(head.getLocation().toVector()) < 4)
                        p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 800, 5, true, true));
                    if(p.getLocation().toVector().distance(neck.getLocation().toVector()) < 4)
                        p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 800, 5, true, true));
                }

            }

            if (timeSinceLastPoisonAttack < POISON_ATTACK_COOLDOWN || timeSinceLastAttack < ATTACK_COOLDOWN)
                break POISON_ATTACK;

            if (new Random().nextInt(POISON_ATTACK_PROPABILITY) != 0)
                break POISON_ATTACK;

            timeSinceLastPoisonAttack = 0;
            timeSinceLastAttack = 0;
        }

        setHead();

    }

    private void effectLogic() {
        if (health < (MAXIMUM_HEALTH / 4) * 3) {
            drowned.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.35);

            BLINDNESS:{
                if(new Random().nextInt(BLINDNESS_PROPABILITY) == 0)
                    drowned.getWorld().getPlayers().get(new Random().nextInt(drowned.getWorld().getPlayers().size())).addPotionEffect
                            (new PotionEffect(PotionEffectType.BLINDNESS, BLINDNESS_DURATION, 10, false, false));
            }

            /*
            SPAWN: {
                timeSinceLastSpawn++;
                if(timeSinceLastSpawn == SPAWN_EFFECT_COOLDOWN) {
                    timeSinceLastSpawn = 0;

                    if(!(drowned.getTarget() instanceof Player))
                        break SPAWN;
                    if(!isTargetValid((Player) drowned.getTarget(), drowned.getWorld()))
                        break SPAWN;

                    Location spawnLoc = drowned.getTarget().getLocation();
                    spawnLoc.getWorld().spawnParticle(Particle.CLOUD, spawnLoc, 5);

                    if(phase == 2) {
                        new LochnessPiranha(spawnLoc);
                        new LochnessPiranha(spawnLoc);
                    }
                    if(phase == 3) {
                        new LochnessHammerhai(spawnLoc);
                        new LochnessHammerhai(spawnLoc);
                    }
                    if(phase == 4) {
                        new LochnessHai(spawnLoc);
                        new LochnessHai(spawnLoc);
                    }
                }
            }
             */

        }
        if (health < (MAXIMUM_HEALTH / 4) * 2)
            INVIS_EFFECT:{
                timeSinceLastInvisEffect++;

                if (timeSinceLastInvisEffect == 1) {
                    setVisible(false);
                } else if (timeSinceLastInvisEffect == INVISIBILITY_EFFECT_DURATION) {
                    setVisible(true);
                }

                if (timeSinceLastInvisEffect < INVISIBILITY_EFFECT_COOLDOWN)
                    break INVIS_EFFECT;
                if (new Random().nextInt(INVISIBILITY_EFFECT_PROPABILITY) != 0)
                    break INVIS_EFFECT;

                timeSinceLastInvisEffect = 0;
            }

        if (health < (MAXIMUM_HEALTH / 4)) {
            if(phase != 4) {
                phase = 4;
                displayParticles(Particle.REDSTONE, 100, 1, 1, 1, 0, new Particle.DustOptions(Color.RED, 1));
            }
            bossBar.setColor(BarColor.RED);
            bossBar.setTitle("Lochness    " + ChatColor.DARK_RED + "+ Catch Attack");
        }
        else if (health < (MAXIMUM_HEALTH / 4) * 2) {
            if(phase != 3) {
                phase = 3;
                displayParticles(Particle.REDSTONE, 100, 1, 1, 1, 0, new Particle.DustOptions(Color.PURPLE, 1));
            }
            bossBar.setColor(BarColor.PURPLE);
            bossBar.setTitle("Lochness    " + ChatColor.DARK_PURPLE + "+ Invisible");
        }
        else if (health < (MAXIMUM_HEALTH / 4) * 3) {
            if(phase != 2) {
                phase = 2;
                displayParticles(Particle.REDSTONE, 100, 1, 1, 1, 0, new Particle.DustOptions(Color.BLUE, 1));
            }
            bossBar.setColor(BarColor.BLUE);
            bossBar.setTitle("Lochness    " + ChatColor.BLUE + "+ Effects");
        }
    }

    private void loadHitboxes() {
        if (headHitbox == null)
            headHitbox = new Hitbox(head.getLocation(), 1.5, 1, 1.5);
        if (neckHitbox == null)
            neckHitbox = new Hitbox(neck.getLocation(), 1.5, 1, 1.5);

        headHitbox.setLocation(head.getLocation().subtract(new Vector(0.75, -0.5, 0.75)));
        neckHitbox.setLocation(neck.getLocation().subtract(new Vector(0.75, -0.5, 0.75)));

        if (Debug.showHitbox) {
            headHitbox.display();
            neckHitbox.display();
        }

        for (int hitboxIndex = 0; hitboxIndex < bodyHitbox.length; hitboxIndex++) {
            if (bodyHitbox[hitboxIndex] == null)
                bodyHitbox[hitboxIndex] = new Hitbox(body[hitboxIndex].getLocation(), 1.5, 1, 1.5);
            bodyHitbox[hitboxIndex].setLocation(body[hitboxIndex].getLocation().subtract(new Vector(0.75, -0.5, 0.75)));
            if (Debug.showHitbox)
                bodyHitbox[hitboxIndex].display();
        }
        for (int hitboxIndex = 0; hitboxIndex < tailHitbox.length; hitboxIndex++) {
            if (tailHitbox[hitboxIndex] == null)
                tailHitbox[hitboxIndex] = new Hitbox(tail[hitboxIndex].getLocation(), 1.5, 1, 1.5);
            tailHitbox[hitboxIndex].setLocation(tail[hitboxIndex].getLocation().subtract(new Vector(0.75, -0.5, 0.75)));
            if (Debug.showHitbox)
                tailHitbox[hitboxIndex].display();
        }


    }

    private void displayParticles(Particle particle, int count, double offsetX, double offsetY, double offsetZ, double speed, Particle.DustOptions dustOptions) {
        if(particle == Particle.REDSTONE) {
            neck.getWorld().spawnParticle(particle, neck.getLocation(), count, offsetX, offsetY, offsetZ, speed, dustOptions);
            head.getWorld().spawnParticle(particle, head.getLocation(), count, offsetX, offsetY, offsetZ, speed,  dustOptions);
            for (ArmorStand armorStand : body)
                armorStand.getWorld().spawnParticle(particle, armorStand.getLocation(), count, offsetX, offsetY, offsetZ, speed, dustOptions);
            for (ArmorStand armorStand : tail)
                armorStand.getWorld().spawnParticle(particle, armorStand.getLocation(), count, offsetX, offsetY, offsetZ, speed, dustOptions);
        } else {
            neck.getWorld().spawnParticle(particle, neck.getLocation(), count, offsetX, offsetY, offsetZ, speed);
            head.getWorld().spawnParticle(particle, head.getLocation(), count, offsetX, offsetY, offsetZ, speed);
            for (ArmorStand armorStand : body)
                armorStand.getWorld().spawnParticle(particle, armorStand.getLocation(), count, offsetX, offsetY, offsetZ, speed);
            for (ArmorStand armorStand : tail)
                armorStand.getWorld().spawnParticle(particle, armorStand.getLocation(), count, offsetX, offsetY, offsetZ, speed);
        }
    }

    private void setVisible(boolean visible) {
        if (this.visible == visible)
            return;
        this.visible = visible;
        if (visible) {
            setHead();
            ItemStack stack = new ItemStack(Material.FISHING_ROD);
            ItemMeta itemMeta = stack.getItemMeta();

            itemMeta.setCustomModelData(2);
            stack.setItemMeta(itemMeta);

            neck.getEquipment().setHelmet(stack);
            for (int bodyPart = 0; bodyPart < body.length; bodyPart++) {
                if (bodyPart == 3) {
                    itemMeta.setCustomModelData(3);
                    stack.setItemMeta(itemMeta);
                }
                if (bodyPart == 5) {
                    itemMeta.setCustomModelData(4);
                    stack.setItemMeta(itemMeta);
                }
                body[bodyPart].getEquipment().setHelmet(stack);
            }
            itemMeta.setCustomModelData(5);
            stack.setItemMeta(itemMeta);
            for (int tailPart = 0; tailPart < tail.length; tailPart++) {
                tail[tailPart].getEquipment().setHelmet(stack);
            }
        } else {
            ItemStack stack = new ItemStack(Material.AIR);
            drowned.getEquipment().setHelmet(stack);
            neck.getEquipment().setHelmet(stack);
            for (int bodyPart = 0; bodyPart < body.length; bodyPart++)
                body[bodyPart].getEquipment().setHelmet(stack);
            for (int tailPart = 0; tailPart < tail.length; tailPart++)
                tail[tailPart].getEquipment().setHelmet(stack);
        }

        // ---- particles ----
        displayParticles(Particle.FLASH, 1, 0, 0, 0, 0, null);
    }

    private void setHead() {
        ItemStack stack = new ItemStack(Material.FISHING_ROD);
        ItemMeta meta = stack.getItemMeta();
        if(timeSinceLastKnockbackAttack < KNOCKBACK_ATTACK_FEEDBACK + KNOCKBACK_ATTACK_DURATION && timeSinceLastKnockbackAttack > KNOCKBACK_ATTACK_FEEDBACK)
            meta.setCustomModelData(6);
        else if(timeSinceLastFreezeAttack < FREEZE_ATTACK_FEEDBACK + 80 && timeSinceLastFreezeAttack > FREEZE_ATTACK_FEEDBACK)
            meta.setCustomModelData(7);
        else if(timeSinceLastFireAttack < FIRE_ATTACK_FEEDBACK + FIRE_ATTACK_DURATION && timeSinceLastFireAttack > FIRE_ATTACK_FEEDBACK)
            meta.setCustomModelData(8);
        else
            meta.setCustomModelData(1);
        stack.setItemMeta(meta);
        if(visible)
            drowned.getEquipment().setHelmet(stack);
    }

    public static void resetEffectValues() {
        if(BlockBreakEvent.defense_3_broken) {
            DEFENSE_EFFECT_FACTOR = 1;
        } else if(BlockBreakEvent.defense_2_broken) {
            DEFENSE_EFFECT_FACTOR = BlockBreakEvent.DEFENSE_FACTOR_3;
        } else if(BlockBreakEvent.defense_1_broken) {
            DEFENSE_EFFECT_FACTOR = BlockBreakEvent.DEFENSE_FACTOR_2;
        } else{
            DEFENSE_EFFECT_FACTOR = BlockBreakEvent.DEFENSE_FACTOR_1;
        }

        if(BlockBreakEvent.strength_3_broken) {
            STRENGTH_EFFECT_FACTOR = 1;
        } else if(BlockBreakEvent.strength_2_broken) {
            STRENGTH_EFFECT_FACTOR = BlockBreakEvent.STRENGTH_FACTOR_3;
        } else if(BlockBreakEvent.strength_1_broken) {
            STRENGTH_EFFECT_FACTOR = BlockBreakEvent.STRENGTH_FACTOR_2;
        } else{
            STRENGTH_EFFECT_FACTOR = BlockBreakEvent.STRENGTH_FACTOR_1;
        }

        if(BlockBreakEvent.regeneration_3_broken) {
            REGENERATION_EFFECT = 0;
        } else if(BlockBreakEvent.regeneration_2_broken) {
            REGENERATION_EFFECT = BlockBreakEvent.REGENERATION_3;
        } else if(BlockBreakEvent.regeneration_1_broken) {
            REGENERATION_EFFECT = BlockBreakEvent.REGENERATION_2;
        } else{
            REGENERATION_EFFECT = BlockBreakEvent.REGENERATION_1;
        }
    }

    private void resetDrowned(Location loc) {

        if(drowned != null) {
            drowned.remove();
            drowned.setHealth(0);
            drowned.addScoreboardTag("deadLochness");
        }

        drowned = (Drowned) loc.getWorld().spawnEntity(loc, EntityType.DROWNED);
        drowned.setInvisible(true);
        drowned.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 42, 1, false, false));
        drowned.setRemoveWhenFarAway(false);
        drowned.setPersistent(true);
        drowned.leaveVehicle();
        drowned.setInvulnerable(true);
        drowned.setSilent(true);
        drowned.setBaby();
        drowned.setCollidable(false);
        drowned.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(100);
        drowned.addScoreboardTag("lochnessBoss");
        drowned.getEquipment().setItemInMainHand(new ItemStack(Material.COMMAND_BLOCK_MINECART));
        drownedUUID = drowned.getUniqueId();

        ItemStack stack = new ItemStack(Material.FISHING_ROD);
        ItemMeta itemMeta = stack.getItemMeta();
        itemMeta.setCustomModelData(1);
        stack.setItemMeta(itemMeta);

        if (visible)
            drowned.getEquipment().setHelmet(stack);

    }

    private void checkIfDead() {
        boolean tailDead = false;
        boolean bodyDead = false;

        for (int bodyPart = 0; bodyPart < body.length; bodyPart++)
            if (body[bodyPart].isDead())
                bodyDead = true;

        for (int tailPart = 0; tailPart < tail.length; tailPart++)
            if (tail[tailPart].isDead())
                tailDead = true;

        if (drowned.isDead() || dolphin.isDead() || neck.isDead() || head.isDead() || tailDead || bodyDead) {
            remove();
        }
    }

    public void remove() {
        drowned.remove();
        dolphin.remove();
        neck.remove();
        head.remove();
        for (int bodyPart = 0; bodyPart < body.length; bodyPart++)
            body[bodyPart].remove();
        for (int tailPart = 0; tailPart < tail.length; tailPart++)
            tail[tailPart].remove();
        bossBar.setVisible(false);
        bossBar.removeAll();
        stop();
    }

    public void calculateProjectiles() {
        ENTITY:
        for(Entity entity: drowned.getWorld().getEntities()) {

            if(!(entity instanceof Trident))
                continue;

            if(entity.getScoreboardTags().contains("lochnessHitProjectile"))
                continue;

            entity.addScoreboardTag("lochnessHitProjectile");

            for(Hitbox hitbox: getHitboxes()) {
                Location loc = entity.getLocation().clone();
                loc.setDirection(loc.getDirection().multiply(-1));
                Location hitLoc = hitbox.getIntersectionPoint(loc);
                if(hitLoc == null)
                    continue;
                if(hitLoc.distance(loc) < 1) {
                    damadge(((Trident)entity).getDamage());
                    for(Player player: drowned.getWorld().getPlayers()) {
                        player.setCooldown(Material.TRIDENT, 500);
                    }
                    continue ENTITY;
                }
            }
        }
    }

    public void damadge(double damadge) {
        health = health - (damadge * DEFENSE_EFFECT_FACTOR);
        if (health < 0) {
            setVisible(true);
            health = 0;
            dolphin.remove();
            drowned.remove();

            displayParticles(Particle.REDSTONE, 10, 1, 1, 1, 0, new Particle.DustOptions(Color.WHITE, 5));
            displayParticles(Particle.REDSTONE, 10, 1, 1, 1, 0, new Particle.DustOptions(Color.RED, 5));
            displayParticles(Particle.FLASH, 1, 0, 0, 0, 0, null);

            neck.setGravity(true);
            head.setGravity(true);
            for (ArmorStand armorStand : body)
                armorStand.setGravity(true);
            for (ArmorStand armorStand : tail)
                armorStand.setGravity(true);
            bossBar.setVisible(false);
            bossBar.removeAll();
            ItemStack stack = new ItemStack(Material.FISHING_ROD);
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setCustomModelData(1);
            stack.setItemMeta(itemMeta);
            head.getEquipment().setHelmet(stack);
            stop();

            for(Player p: Bukkit.getOnlinePlayers()) {
                PluginUtils.grantAdvancement(p, "lochness:lochness_and_then");
            }
            Bukkit.getScheduler().runTaskLater(Lochness.getPlugin(), () -> {
                MessageHandler.startEndcredits();
            }, 200);
        }
        if (health > MAXIMUM_HEALTH)
            health = MAXIMUM_HEALTH;
    }

    @Override
    protected void save() {
        bossBar.setVisible(false);
        bossBar.removeAll();
        dolphin.teleport(drowned);
        head.teleport(drowned);
        neck.teleport(drowned);
        for (ArmorStand bodyArmorStand : body)
            bodyArmorStand.teleport(drowned);
        for (ArmorStand tailArmorStand : tail)
            tailArmorStand.teleport(drowned);

        world = Bukkit.getWorlds().indexOf(drowned.getWorld());
        chunkX = drowned.getLocation().getChunk().getX();
        chunkZ = drowned.getLocation().getChunk().getZ();
    }

    private enum AI_STATE implements Serializable {
        RANDOM_MOVEMENT,
        ATTACK
    }

    public ArrayList<Hitbox> getHitboxes() {
        ArrayList<Hitbox> hitboxes = new ArrayList<>();
        hitboxes.add(headHitbox);
        hitboxes.add(neckHitbox);
        for (Hitbox bodyHitbox : bodyHitbox)
            hitboxes.add(bodyHitbox);
        for (Hitbox tailHitbox : tailHitbox)
            hitboxes.add(tailHitbox);
        return hitboxes;
    }

}

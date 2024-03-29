package me.karl.lochness.entities.ceberos;

import me.karl.lochness.entities.LochnessEntity;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class Cerberus extends LochnessEntity {

    public static final long serialVersionUID = 2;

    private static final int TRIGGER_TIME = 6000;

    private transient ArmorStand armorStand;
    private UUID armorStandUUID;

    private transient Wolf wolf;
    private UUID wolfUUID;

    transient Vector lastDirection;
    transient Vector lastPosition;
    boolean wasMoving = false;
    boolean isMoving = false;
    boolean isTurning = false;
    boolean isSitting = true;
    transient ArrayList<String> triggerPlayers = new ArrayList<>();
    String targetName = "";
    int trigger = 0;
    int animation = 0;

    public Cerberus(Cerberus cerberus) {
        super(cerberus.world, cerberus.chunkX, cerberus.chunkZ);

        wolf = (Wolf) Bukkit.getEntity(cerberus.wolfUUID);
        if (wolf != null) {
            wolfUUID = wolf.getUniqueId();
            lastPosition = wolf.getLocation().toVector();
            lastDirection = wolf.getLocation().getDirection();
        }

        armorStand = (ArmorStand) Bukkit.getEntity(cerberus.armorStandUUID);
        if (armorStand != null) {
            armorStandUUID = armorStand.getUniqueId();
        }

        wasMoving = cerberus.wasMoving;
        isMoving = cerberus.isMoving;
        isTurning = cerberus.isTurning;
        isSitting = cerberus.isSitting;

        targetName = cerberus.targetName;

        trigger = cerberus.trigger;
        animation = cerberus.animation;

    }

    public Cerberus(Location loc) {
        super(Bukkit.getWorlds().indexOf(loc.getWorld()), loc.getChunk().getX(), loc.getChunk().getZ());

        armorStand = (ArmorStand) Objects.requireNonNull(loc.getWorld()).spawnEntity(loc, EntityType.ARMOR_STAND);
        armorStandUUID = armorStand.getUniqueId();

        wolf = (Wolf) loc.getWorld().spawnEntity(loc, EntityType.WOLF);
        wolfUUID = wolf.getUniqueId();

        armorStand.setInvisible(true);
        armorStand.setSmall(true);
        armorStand.addScoreboardTag("ceberos");
        armorStand.setSilent(true);

        ItemStack customModel = new ItemStack(Material.BONE);
        ItemMeta meta = customModel.getItemMeta();
        meta.setCustomModelData(3);
        customModel.setItemMeta(meta);
        armorStand.getEquipment().setHelmet(customModel);

        wolf.setAngry(true);
        wolf.setRemoveWhenFarAway(false);
        wolf.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 42, 255, true, false));
        wolf.setCollarColor(DyeColor.BLACK);
        wolf.setAdult();
        wolf.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(100);
        wolf.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(20);
        wolf.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(500);
        wolf.setHealth(100);
        wolf.addScoreboardTag("ceberos");

        lastPosition = wolf.getLocation().toVector();
        lastDirection = wolf.getLocation().getDirection();
    }

    @Override
    public Runnable tick() {
        return () -> {

            tagEntities(LochnessEntity.entityValidityTag, LochnessEntity.entityValidityNum);

            if (wolf == null || armorStand == null) {
                remove();
                return;
            }

            // Only calculate if player is near
            Boolean nearbyPlayer = false;
            for(Entity entity: wolf.getNearbyEntities(80.0, 80.0, 80.0)) {
                if (entity.getType() == EntityType.PLAYER)
                    nearbyPlayer = true;
            }
            if (!nearbyPlayer)
                return;

            // --------------------- if dead -----------------------------------
            if(wolf.getHealth() == 0.0) {
                remove();
            }

            // ---------------------- some useful variables -----------------------
            isMoving = !lastPosition.equals(wolf.getLocation().toVector());
            if(isMoving)
                lastPosition = wolf.getLocation().toVector();

            isTurning = lastDirection.distance(wolf.getLocation().getDirection()) > 0.5;
            if(isTurning)
                lastDirection = wolf.getLocation().getDirection();

            // ----------------------- animate (custom model data) -----------------------
            ItemStack customModel = new ItemStack(Material.BONE);
            ItemMeta meta = customModel.getItemMeta();
            if(isMoving) {
                animation++;
                if (animation >= 10)
                    animation = 0;

                meta.setCustomModelData(animation / 5 + 2);
            }
            else
                meta.setCustomModelData(1);

            if(isSitting)
                meta.setCustomModelData(4);

            customModel.setItemMeta(meta);
            armorStand.getEquipment().setHelmet(customModel);

            // ----------------------- custom model movement ----------------------------------
            wolf.setSitting(isSitting);

            Location armorStandLoc = wolf.getLocation().clone();
            if(!isTurning && !(!wasMoving && isMoving))
                armorStandLoc.setDirection(lastDirection);
            armorStand.teleport(armorStandLoc);

            // ---------------------- trigger code -----------------------------------
            trigger --;
            if(trigger < -4800)
                trigger = 0;

            Player triggeredPlayer = Bukkit.getPlayer(targetName);
            if(targetName == "");
            else if(triggeredPlayer == null)
                resetTrigger();
            else if(triggeredPlayer.getGameMode() == GameMode.CREATIVE)
                resetTrigger();
            else if(triggeredPlayer.getGameMode() == GameMode.SPECTATOR)
                resetTrigger();
            else if(!triggeredPlayer.isOnline())
                resetTrigger();
            else if(trigger >= 0) {
                wolf.setAngry(true);
                isSitting = false;
                wolf.setTarget(triggeredPlayer);
            }else if(trigger < -3600){
                resetTrigger();
                isSitting = true;
            }else {
                resetTrigger();
            }

            A:
            if(wolf.getTarget() == null) {
                if(triggerPlayers.size() <= 0)
                    break A;

                int index = new Random().nextInt(triggerPlayers.size());
                Player player = Bukkit.getPlayer(triggerPlayers.get(index));
                if(triggeredPlayer == null)
                    triggerPlayers.remove(player.getName());
                if(triggeredPlayer.getGameMode() == GameMode.CREATIVE)
                    triggerPlayers.remove(player.getName());
                if(triggeredPlayer.getGameMode() == GameMode.SPECTATOR)
                    triggerPlayers.remove(player.getName());
                if(!triggeredPlayer.isOnline())
                    triggerPlayers.remove(player.getName());

                if(triggerPlayers.contains(player.getName()))
                    targetName = player.getName();
            }

            // ---------------------- hitboxes and stuff -----------------------------

            wolf.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 42, 255, true, false));
            wolf.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 42, 1, true, false));

            // ----------------------- preperation for next tick -----------------------------------------
            wasMoving = isMoving;

        };
    }

    @Override
    public void updateGlobalChunkPos() {
        if (wolf == null)
            return;
        if (!wolf.getLocation().getChunk().isLoaded())
            return;
        chunkX = wolf.getLocation().getChunk().getX();
        chunkZ = wolf.getLocation().getChunk().getZ();
        world = Bukkit.getWorlds().indexOf(wolf.getWorld());
    }

    @Override
    protected void tagEntities(String validityTag, long num) {
        loadEntityByUIDs();
        LochnessEntity.tagEntity(wolf, validityTag, num);
        LochnessEntity.tagEntity(armorStand, validityTag, num);
    }

    @Override
    protected void loadEntityByUIDs() {
        wolf = wolfUUID == null ? wolf : (Wolf) (Bukkit.getEntity(wolfUUID) == null ? wolf : Bukkit.getEntity(wolfUUID));
        armorStand = armorStandUUID == null ? armorStand : (ArmorStand) (Bukkit.getEntity(armorStandUUID) == null ? armorStand : Bukkit.getEntity(armorStandUUID));
    }

    @Override
    protected ArrayList<UUID> getEntityUUIDs() {
        ArrayList<UUID> list = new ArrayList<>();
        list.add(wolfUUID);
        list.add(armorStandUUID);
        return list;
    }

    @Override
    public void remove() {
        super.remove();
        if (wolf != null)
            wolf.remove();
        if (armorStand != null)
            armorStand.remove();
    }

    private void resetTrigger() {
        wolf.setTarget(null);
        triggerPlayers.remove(targetName);
        targetName = "";
        wolf.setAngry(false);
    }

    public void trigger(Player player) {
        trigger = TRIGGER_TIME;
        if(!triggerPlayers.contains(player.getName()))
            triggerPlayers.add(player.getName());
    }

    @Override
    protected void save() {
        if (wolf == null || armorStand == null)
            return;
        armorStand.teleport(wolf);
        world = Bukkit.getWorlds().indexOf(wolf.getWorld());
        chunkX = wolf.getLocation().getChunk().getX();
        chunkZ = wolf.getLocation().getChunk().getZ();
    }

}

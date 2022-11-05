package me.karl.lochness.entities;

import me.karl.lochness.Lochness;
import me.karl.lochness.PluginUtils;
import me.karl.lochness.entities.lochness.LochnessBoss;
import me.karl.lochness.entities.watermonsters.WaterMonster;
import me.karl.lochness.entities.watermonsters.kraken.LochnessKraken;
import org.bukkit.*;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;

import java.util.*;

public class LochnessEntityHitEvent implements Listener {

    public static final double PLAYER_HIT_RANGE = 4;
    public static final int COOLDOWN_TIME = 10;
    public static final HashMap<UUID, Integer> cooldown = new HashMap<>();

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!event.getDamager().getLocation().getWorld().getEnvironment().equals(World.Environment.THE_END))
            return;
        if (!(event.getDamager() instanceof Player))
            return;
        if (!(event.getEntity() instanceof Drowned))
            return;
        hit((Player) event.getDamager(), event.getEntity());
    }

    @EventHandler
    public void onInteraction(PlayerInteractEvent event) {
        if (!event.getPlayer().getLocation().getWorld().getEnvironment().equals(World.Environment.THE_END))
            return;
        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))
            hit(event.getPlayer(), null);
    }

    public void hit(Player player, Entity hitEntity) {

        ENTITIES:
        for (LochnessEntity entity : LochnessEntity.getEntities()) {
            LOCHNESS_BOSS:
            {
                if (!(entity instanceof LochnessBoss))
                    break LOCHNESS_BOSS;
                LochnessBoss boss = (LochnessBoss) entity;
                for (Hitbox hitbox : boss.getHitboxes()) {
                    if (hitbox == null)
                        continue;
                    Location hitLoc = hitbox.getIntersectionPoint(player.getEyeLocation());
                    if (hitLoc == null)
                        continue;
                    if (hitLoc.distance(player.getEyeLocation()) > PLAYER_HIT_RANGE)
                        continue;

                    if (cooldown.containsKey(player.getUniqueId()))
                        break LOCHNESS_BOSS;

                    boss.damadge(PluginUtils.calculateDamadge(player.getEquipment().getItemInMainHand(), player));
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_FRAME_PLACE, 1, 1);
                    addPlayerCooldown(player);
                    player.setCooldown(player.getEquipment().getItemInMainHand().getType(), COOLDOWN_TIME);
                    Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 2);
                    hitLoc.getWorld().spawnParticle(Particle.REDSTONE, hitLoc, 3, dustOptions);
                    if (new Random().nextInt(2) == 0) {
                        if (boss.getHitboxes().indexOf(hitbox) > 1)
                            PluginUtils.damadgePlayer(player, 90 * LochnessBoss.STRENGTH_EFFECT_FACTOR, "LOCHNESS_1");
                        player.setVelocity(player.getVelocity().add(player.getLocation().subtract(hitLoc).toVector().normalize().multiply(1)));
                        hitLoc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, hitLoc, 1);
                    }

                    //sound
                    ArrayList<Entity> soundRecievers = new ArrayList<>();
                    for (UUID entityUUID: boss.getEntityUUIDs())
                    {
                        soundRecievers.addAll(Objects.requireNonNull(Bukkit.getEntity(entityUUID)).getNearbyEntities(5, 5, 5));
                    }
                    if (boss.getHitboxes().indexOf(hitbox) > 1)
                    {
                        for(Entity e: soundRecievers)
                        {
                            if(e instanceof Player)
                                ((Player)e).playSound(e.getLocation(), Sound.ENTITY_GUARDIAN_HURT, 1, 1);
                        }
                    }
                    else
                    {
                        for(Entity e: soundRecievers)
                        {
                            if(e instanceof Player)
                                ((Player)e).playSound(e.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_HURT, 1, 1.5f);
                        }
                    }

                    break ENTITIES;
                }
            }

            WATER_MONSTER:
            {
                if (!(entity instanceof WaterMonster))
                    break WATER_MONSTER;

                WaterMonster monster = (WaterMonster) entity;
                if (!monster.isLoaded())
                    break WATER_MONSTER;

                if (monster.getLocation().getWorld().getEnvironment() != player.getWorld().getEnvironment())
                    break WATER_MONSTER;

                if (monster.getLocation().distance(player.getLocation()) > 8)
                    break WATER_MONSTER;

                Boolean hitDrowned = false;
                if (hitEntity != null && monster.getEntityUUIDs().contains(hitEntity.getUniqueId()))
                    hitDrowned = true;

                if (monster.getHitbox() == null)
                    break WATER_MONSTER;
                Location hitLoc = monster.getHitbox().getIntersectionPoint(player.getEyeLocation());
                if (!hitDrowned) {
                    if (hitLoc == null)
                        break WATER_MONSTER;
                    if (hitLoc.distance(player.getEyeLocation()) > PLAYER_HIT_RANGE)
                        break WATER_MONSTER;
                }
                else
                {
                    hitLoc = hitEntity.getLocation();
                }

                if (cooldown.containsKey(player.getUniqueId())) {
                    break WATER_MONSTER;
                }


                if (monster instanceof LochnessKraken) {
                    if (player.getScoreboardTags().contains("angesaugt")) {
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SLIME_BLOCK_BREAK, 1, 1);
                        break WATER_MONSTER;
                    } else {
                        if (monster.drowned.getTarget() != null)
                            monster.drowned.getTarget().removeScoreboardTag("angesaugt");
                        ((LochnessKraken) monster).stopSucking();
                    }
                }

                addPlayerCooldown(player);
                player.setCooldown(player.getEquipment().getItemInMainHand().getType(), COOLDOWN_TIME);
                monster.damadge(PluginUtils.calculateDamadge(player.getEquipment().getItemInMainHand(), player));
                monster.drowned.setVelocity(monster.drowned.getVelocity().clone().add(monster.drowned.getLocation().clone().subtract(player.getLocation()).toVector().normalize().multiply(0.2)));
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 2);
                hitLoc.getWorld().spawnParticle(Particle.REDSTONE, hitLoc, 3, dustOptions);
                break ENTITIES;
            }
        }
    }

    public void addPlayerCooldown(Player player) {
        cooldown.put(player.getUniqueId(), Bukkit.getScheduler().runTaskLater(Lochness.getPlugin(), new Runnable() {
            @Override
            public void run() {
                cooldown.remove(player.getUniqueId());
            }
        }, COOLDOWN_TIME).getTaskId());
    }
}

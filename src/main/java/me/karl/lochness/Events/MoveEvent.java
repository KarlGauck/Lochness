package me.karl.lochness.Events;

import me.karl.lochness.Lochness;
import me.karl.lochness.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class MoveEvent implements Listener {

    @EventHandler
    public void onFall(PlayerMoveEvent event) {

        VISIBILITY:
        {
            Location fallTubeLoc = Lochness.getIslandLoc().add(new Vector(0, event.getTo().getY(), 7));
            Location portal = Lochness.getIslandLoc().add(new Vector(16, 39, 23));

            for(Player p: Bukkit.getOnlinePlayers()) {
                if(p.getLocation().getWorld().getEnvironment() != World.Environment.NORMAL || p.getLocation().distance(portal) > 18) {
                    if(event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL && fallTubeLoc.distance(event.getTo()) > 6) {
                        for (Player p1 : Bukkit.getOnlinePlayers()) {
                            if (p1.getLocation().getWorld().getEnvironment() != World.Environment.NORMAL || p1.getLocation().distance(portal) > 18) {
                                showPlayers(p, p1);
                            } else {
                                hidePlayers(p, p1);
                            }
                        }
                    }
                } else {
                    for(Player p1: Bukkit.getOnlinePlayers()) {
                        if(p1.getLocation().getWorld().getEnvironment() != World.Environment.NORMAL || p1.getLocation().distance(portal) > 18) {
                            hidePlayers(p, p1);
                        } else {
                            showPlayers(p, p1);
                        }
                    }
                }
            }
        }

        FALL:
        {
            Player player = event.getPlayer();
            Location playerLoc = player.getLocation();
            Location fallTubeLoc = Lochness.getIslandLoc().add(new Vector(0, playerLoc.getY(), 7));

            if (player == null)
                break FALL;
            if (playerLoc == null)
                break FALL;
            if (fallTubeLoc == null)
                break FALL;
            if (playerLoc.getWorld().getEnvironment() != World.Environment.NORMAL)
                break FALL;
            if (playerLoc.distance(fallTubeLoc) > 5)
                break FALL;
            if (playerLoc.getY() > 60)
                break FALL;

            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 32, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 42, 1, true, false));
            if(!player.getScoreboardTags().contains("falling_ready")) {
                for(Player p: Bukkit.getOnlinePlayers()) {
                    if(!p.getUniqueId().equals(player.getUniqueId())) {
                        hidePlayers(player, p);
                    }
                }
            }

            Vector velocity = player.getVelocity();

            if (playerLoc.getY() <= 43)
                player.teleport(playerLoc.add(new Vector(0, 15 , 0)));
            else
                break FALL;

            if (player.getScoreboardTags().contains("falling_ready")) {
                player.teleport(playerLoc.add(new Vector(17, -5, 3)));
                removeScoreboardTag(player);
                for(Player p: Bukkit.getOnlinePlayers()) {
                    showPlayers(player, p);
                }
            }

            addScoreboardTag(player);

            player.setVelocity(velocity);
        }

        PORTAL_ENTER:
        {
            Location portal = Lochness.getIslandLoc().add(new Vector(16, 39, 23));
            Location playerLoc = event.getTo();

            if(playerLoc.getBlockZ() != portal.getBlockZ())
                break PORTAL_ENTER;
            if(!(playerLoc.getBlockY() > portal.getBlockY() - 2 && playerLoc.getBlockY() < portal.getBlockY() + 4))
                break PORTAL_ENTER;
            if(!(playerLoc.getBlockX() > portal.getBlockX() - 3 && playerLoc.getBlockX() < portal.getBlockX() + 3))
                break PORTAL_ENTER;

            Location tpLoc = new Location(Bukkit.getWorlds().get(2), 676, 67, 207);
            tpLoc.setDirection(event.getPlayer().getLocation().getDirection());
            event.getPlayer().teleport(tpLoc);
            for(Player p: Bukkit.getOnlinePlayers()) {
                showPlayers(event.getPlayer(), p);
            }
        }

        PORTAL_LEAVE:
        {
            Location portal = new Location(Bukkit.getWorlds().get(2), 676, 68, 206);

            Location playerLoc = event.getTo();

            if(playerLoc.getBlockZ() != portal.getBlockZ())
                break PORTAL_LEAVE;
            if(!(playerLoc.getBlockY() > portal.getBlockY() - 2 && playerLoc.getBlockY() < portal.getBlockY() + 4))
                break PORTAL_LEAVE;
            if(!(playerLoc.getBlockX() > portal.getBlockX() - 3 && playerLoc.getBlockX() < portal.getBlockX() + 3))
                break PORTAL_LEAVE;

            event.getPlayer().teleport(Lochness.getIslandLoc().add(new Vector(0, 69, 0)));
            for(Player p: Bukkit.getOnlinePlayers()) {
                showPlayers(event.getPlayer(), p);
            }
        }

        PORTAL_BUG:
        {
            Location portal = new Location(Bukkit.getWorlds().get(2), 676, 68, 206);

            Location playerLoc = event.getTo();

            if(!(playerLoc.getBlockZ() > portal.getBlockZ() - 2 && playerLoc.getBlockZ() < portal.getBlockZ() + 4))
                break PORTAL_BUG;
            if(!(playerLoc.getBlockY() >= 101))
                break PORTAL_BUG;
            if(!(playerLoc.getBlockX() > portal.getBlockX() - 3 && playerLoc.getBlockX() < portal.getBlockX() + 3))
                break PORTAL_BUG;

            event.getPlayer().teleport(new Location(Bukkit.getWorlds().get(2), 676, 67, 207));
        }

        ROOM_ENTER:
        {
            Location loc = event.getTo();
            if(loc.getWorld().getEnvironment() != Lochness.getRoomLoc().getWorld().getEnvironment())
                break ROOM_ENTER;
            if(loc.getX() < Lochness.getRoomLoc().getX())
                break ROOM_ENTER;
            if(loc.getX() > Lochness.getRoomLoc().getX() + 48)
                break ROOM_ENTER;;
            if(loc.getZ() < Lochness.getRoomLoc().getZ())
                break ROOM_ENTER;
            if(loc.getZ() > Lochness.getRoomLoc().getZ() + 48)
                break ROOM_ENTER;

            PluginUtils.grantAdvancement(event.getPlayer(), "lochness:dangerous_dungeon");
        }

        ISLAND_ENTER:
        {
            Location loc = event.getTo();
            if(loc.getWorld().getEnvironment() != Lochness.getIslandLoc().getWorld().getEnvironment())
                break ISLAND_ENTER;
            Location distLoc = loc.clone();
            distLoc.setY(0);
            if(distLoc.distance(Lochness.getIslandLoc()) < 50)
                PluginUtils.grantAdvancement(event.getPlayer(), "lochness:majestic_island");
        }

    }

    private void addScoreboardTag(Player p) {
        String tag = "";

        if(p.getScoreboardTags().contains("falling_1"))
            tag = "falling_2";
        else if(p.getScoreboardTags().contains("falling_2"))
            tag = "falling_3";
        else if(p.getScoreboardTags().contains("falling_3"))
            tag = "falling_4";
        else if(p.getScoreboardTags().contains("falling_4"))
            tag = "falling_5";
        else if(p.getScoreboardTags().contains("falling_5"))
            tag = "falling_ready";
        else if(!p.getScoreboardTags().contains("falling_ready"))
            tag = "falling_1";

        removeScoreboardTag(p);

        p.addScoreboardTag(tag);
    }

    private void removeScoreboardTag(Player p) {
        p.removeScoreboardTag("falling_1");
        p.removeScoreboardTag("falling_2");
        p.removeScoreboardTag("falling_3");
        p.removeScoreboardTag("falling_4");
        p.removeScoreboardTag("falling_5");
        p.removeScoreboardTag("falling_ready");
    }

    private void showPlayers(Player p1, Player p2) {
        if(!p1.canSee(p2))
            p1.showPlayer(Lochness.getPlugin(), p2);
        if(!p2.canSee(p1))
            p2.showPlayer(Lochness.getPlugin(), p1);
    }

    private void hidePlayers(Player p1, Player p2) {
        if(p1.canSee(p2))
            p1.hidePlayer(Lochness.getPlugin(), p2);
        if(p2.canSee(p1))
            p2.hidePlayer(Lochness.getPlugin(), p1);
    }

}

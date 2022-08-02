package me.karl.lochness.Events;

import me.karl.lochness.Lochness;
import me.karl.lochness.entities.LochnessEntity;
import me.karl.lochness.entities.lochness.LochnessBoss;
import me.karl.lochness.structures.cave.CaveLogic;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Objects;

public class DeathEvent implements Listener {

    public static int playerDeaths = 0;

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        String playerName = event.getEntity().getDisplayName();
        String cause = Objects.requireNonNull(event.getDeathMessage()).substring(event.getDeathMessage().length() - 10);
        Player player = event.getEntity();
        switch (cause) {
            case "LOCHNESS_1":
                event.setDeathMessage(playerName + " died, trying to brutaly murder an innocent creature from behind");
                event.setKeepInventory(true);
                event.getDrops().clear();
                break;
            case "LOCHNESS_2":
                event.setDeathMessage(playerName + " got stuck between sharp teeths");
                event.setKeepInventory(true);
                event.getDrops().clear();
                break;
            case "LOCHNESS_3":
                event.setDeathMessage(playerName + " mistook fire for ice");
                event.setKeepInventory(true);
                event.getDrops().clear();
                break;
            case "LOCHNESS_4":
                event.setDeathMessage(playerName + " was too slow");
                event.setKeepInventory(true);
                event.getDrops().clear();
                break;
            case "LOCHNESS_5":
                event.setDeathMessage(playerName + " was thrown around by Lochness");
                event.setKeepInventory(true);
                event.getDrops().clear();
                break;
            case "WMONSTER_1":
                event.setDeathMessage(playerName + " was impaled");
                event.setKeepInventory(true);
                event.getDrops().clear();
                break;
            case "WMONSTER_2":
                event.setDeathMessage(playerName + " misunderstood the word 'fight'");
                event.setKeepInventory(true);
                event.getDrops().clear();
                break;
            case "WMONSTER_3":
                event.setDeathMessage(playerName + " was killed by a dangerous prisoner");
                event.setKeepInventory(true);
                event.getDrops().clear();
                break;
        }

        if (cause.contains("LOCHNESS")) {
            playerDeaths ++;

            for(Player p: Bukkit.getWorlds().get(2).getPlayers()) {
                String title = ChatColor.BOLD + "Deaths: " + ChatColor.GREEN + playerDeaths + "/10";
                if(playerDeaths == 8)
                    title = ChatColor.BOLD + "Deaths: " + ChatColor.YELLOW + playerDeaths + "/10";
                if(playerDeaths == 9)
                    title = ChatColor.BOLD + "Deaths: " + ChatColor.DARK_RED + playerDeaths + "/10";
                if(playerDeaths == 10)
                    title = ChatColor.BOLD + "Deaths: " + ChatColor.ITALIC + playerDeaths + "/10";
                p.sendTitle("", title, 20, 120, 20);
            }

            if (playerDeaths >= 10) {
                CaveLogic.resetCave();
                Bukkit.broadcastMessage(Lochness.getPrefix() + ChatColor.YELLOW + "You died more than 10 times in one session, the cave has been reset");
                Bukkit.broadcastMessage(Lochness.getPrefix() + ChatColor.YELLOW + "Have you beaten the imprisoned monsters yet, to weaken Lochness?");

                //Remove items from players inventory
                for (Player p: Bukkit.getOnlinePlayers()) {
                    Inventory i = p.getInventory();
                    for(int ind = i.getContents().length-1; ind >= 0; ind--) {
                        ItemStack s = i.getContents()[ind];
                        if(s == null)
                            continue;
                        if(s.getType() != Material.IRON_NUGGET)
                            continue;
                        if(s.hasItemMeta() && Objects.requireNonNull(s.getItemMeta()).hasCustomModelData() && s.getItemMeta().getCustomModelData() != 0) {
                            i.remove(s);
                        }
                    }
                }


                for(LochnessEntity e: LochnessEntity.getEntities()) {
                    if(e instanceof LochnessBoss) {
                        ((LochnessBoss)e).bossBar.removePlayer(player);
                    }
                }

                playerDeaths = 0;
            }

        }

        Location loc = event.getEntity().getLocation();
        if(!Objects.requireNonNull(loc.getWorld()).getEnvironment().equals(World.Environment.THE_END))
            return;

        if(loc.getBlockX() < 544 || loc.getBlockX() > 544 + (48 * 3))
            return;

        if(loc.getBlockZ() < 144 || loc.getBlockZ() > 144 + (48 * 3))
            return;

        player.setBedSpawnLocation(Lochness.getIslandLoc().add(new Vector(0, 67, 0)), true);

        Bukkit.getWorlds().get(2).setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        Bukkit.getScheduler().runTaskLater(Lochness.getPlugin(), () -> Bukkit.getWorlds().get(2).setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false), 2);

        event.setKeepInventory(true);
        event.getDrops().clear();
    }

}

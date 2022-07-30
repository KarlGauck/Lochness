package me.karl.lochness.Events;

import me.karl.lochness.Lochness;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class IslandBlockEvent implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation().clone();
        if (loc.getWorld().getEnvironment() != World.Environment.NORMAL)
            return;
        loc.setY(Lochness.getIslandLoc().getY());
        if (loc.distance(Lochness.getIslandLoc()) > 40)
            return;
        loc = event.getBlock().getLocation();
        PersistentDataContainer container = event.getBlock().getChunk().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Lochness.getPlugin(), "playerblock_" + loc.getX() + "_" + loc.getY() + "_" + loc.getZ());
        if (!container.has(key, PersistentDataType.BYTE) || container.get(key, PersistentDataType.BYTE).byteValue() != 1) {
            event.setCancelled(true);
            return;
        } else {
            container.set(key, PersistentDataType.BYTE, (byte)0);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Location loc = event.getBlock().getLocation().clone();
        if (loc.getWorld().getEnvironment() != World.Environment.NORMAL)
            return;
        loc.setY(Lochness.getIslandLoc().getY());
        if (loc.distance(Lochness.getIslandLoc()) > 40)
            return;
        loc = event.getBlock().getLocation();
        PersistentDataContainer container = event.getBlock().getChunk().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Lochness.getPlugin(), "playerblock_" + loc.getX() + "_" + loc.getY() + "_" + loc.getZ());
        container.set(key, PersistentDataType.BYTE, (byte)1);
    }

}

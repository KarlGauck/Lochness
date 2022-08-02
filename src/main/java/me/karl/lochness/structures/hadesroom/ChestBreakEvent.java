package me.karl.lochness.structures.hadesroom;

import me.karl.lochness.Lochness;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;

public class ChestBreakEvent implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location l1 = event.getBlock().getLocation();
        Location l2 = Lochness.getRoomLoc().clone().add(new Vector(12, 8, 40));
        if (l1.getWorld() == l2.getWorld() && l1.getBlockX() == l2.getBlockX() && l1.getBlockY() == l2.getBlockY() && l1.getBlockZ() == l2.getBlockZ())
            event.setCancelled(true);
    }

}

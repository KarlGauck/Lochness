package me.karl.lochness.structures.cave;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BlockPlaceEvent implements Listener {

    @EventHandler
    public void onBlockBreak(org.bukkit.event.block.BlockPlaceEvent event) {
        Player p = event.getPlayer();
        Block block = event.getBlock();
        Location loc = block.getLocation();

        if (p.getGameMode().equals(GameMode.CREATIVE))
            return;

        if (!loc.getWorld().getEnvironment().equals(World.Environment.THE_END))
            return;

        if (loc.getBlockX() < 544 || loc.getBlockX() > 544 + (48 * 3))
            return;

        if (loc.getBlockZ() < 144 || loc.getBlockZ() > 144 + (48 * 3))
            return;

        event.setCancelled(true);
    }

}

package me.karl.lochness.structures.hadesroom;

import me.karl.lochness.Lochness;
import me.karl.lochness.entities.ceberos.Cerberus;
import me.karl.lochness.structures.StructureLoader;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.util.Vector;

public class RoomLoadEvent implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();

        if(Lochness.getRoomLoc() == null)
            return;

        if(!Lochness.getRoomLoc().getChunk().equals(chunk))
            return;

        if(!chunk.isLoaded())
            return;

        if(Lochness.getRoomLoc().add(new Vector(0, 2, 0)).getBlock().getType() == Material.END_PORTAL_FRAME)
            return;

        if(!chunk.getWorld().getEnvironment().equals(World.Environment.NETHER))
            return;

        StructureLoader.generateStructure(Lochness.getRoomLoc(), "lochness","lochness:hades_room");
        new Cerberus(Lochness.getRoomLoc().clone().add(new Vector(4, 16, 12)));

    }

}

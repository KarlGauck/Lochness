package me.karl.lochness.structures.islands;

import me.karl.lochness.Lochness;
import me.karl.lochness.structures.StructureLoader;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Random;

public class IslandLoadEvent implements Listener {

    Random ran = new Random();
    public static boolean islandRegenerated = true;

    @EventHandler
    public void onChunkLoad(org.bukkit.event.world.ChunkLoadEvent event) {

        if(islandRegenerated) {
            if (!event.isNewChunk())
                return;
        }
        if(!event.getChunk().isLoaded())
            return;

        Location towerLoc1 = Lochness.getIslandLoc().clone();
        towerLoc1.setX(towerLoc1.getX() + 50);

        Location towerLoc2 = Lochness.getIslandLoc().clone();
        towerLoc1.setX(towerLoc2.getX() - 50);

        Location towerLoc3 = Lochness.getIslandLoc().clone();
        towerLoc1.setZ(towerLoc3.getZ() + 50);

        Location towerLoc4 = Lochness.getIslandLoc().clone();
        towerLoc1.setZ(towerLoc4.getZ() - 50);


        Chunk chunk = event.getChunk();

        A:
        if (Lochness.getIslandLoc().getChunk().equals(chunk) || towerLoc1.getChunk().equals(chunk) || towerLoc2.getChunk().equals(chunk) ||
                towerLoc3.getChunk().equals(chunk) || towerLoc4.getChunk().equals(chunk)) {

            if (StructureLoader.areIslandsGenerated)
                break A;

            if (!towerLoc1.getChunk().isLoaded())
                break A;
            if (!towerLoc2.getChunk().isLoaded())
                break A;
            if (!towerLoc3.getChunk().isLoaded())
                break A;
            if (!towerLoc4.getChunk().isLoaded())
                break A;

            islandRegenerated = true;
            StructureLoader.generateIslands();
        }
    }

}

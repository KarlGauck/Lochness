package me.karl.lochness.structures.cage;

import me.karl.lochness.structures.StructureLoader;
import org.bukkit.*;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Strider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

public class CageLoadEvent implements Listener {

    private static Random ran = new Random();
    private static ArrayList<Location> chestLoc = new ArrayList<>();

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {

        Chunk chunk = event.getChunk();

        if (!chunk.getWorld().getEnvironment().equals(World.Environment.NETHER))
            return;

        if (!chunk.isLoaded())
            return;

        if (!event.isNewChunk())
            return;

        // ------------------------------------------- GENERATE LOOT TABLES FOR CHESTS ----------------------------------------------------------

        Iterator<Location> chestLocIterator = chestLoc.iterator();
        while (chestLocIterator.hasNext()) {
            Location iteratorLoc = chestLocIterator.next();
            if (!iteratorLoc.getChunk().isLoaded())
                continue;

            Location loc = iteratorLoc.clone();

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in minecraft:the_nether run setblock " + loc.getBlockX() + " " + loc.getBlockY() +
                    " " + loc.getBlockZ() + " chest{LootTable:\"lochness:chests/cage_kitchen\"}");

            chestLocIterator.remove();
        }

        // ------------------------------------------------ GENERATE STRUCTURES ----------------------------------------------------------------

        Location loc = event.getChunk().getBlock(0, 30, 0).getLocation();

        ran.setSeed(Long.parseLong("" + Math.abs((chunk.getX() + 200000) % 1319947) + Math.abs((chunk.getZ() + 200000) % 1319947) +
                Math.abs((Bukkit.getWorlds().get(1).getSeed() % 1319947))));
        boolean canChunkHaveCageBlob = ran.nextInt(100) == 0;

        if (!canChunkHaveCageBlob)
            return;

        loc = getValidCageloc(loc);
        if (loc == null)
            return;

        // Generate middle structure
        generateRandomStructure(loc, true);

        // Generate outer structures (50%)
        if (ran.nextBoolean())
            generateRandomStructure(new Location(Bukkit.getWorlds().get(1), loc.getX() + 16, loc.getY(), loc.getZ() - 16), false);
        if (ran.nextBoolean())
            generateRandomStructure(new Location(Bukkit.getWorlds().get(1), loc.getX(), loc.getY(), loc.getZ() - 16), false);
        if (ran.nextBoolean())
            generateRandomStructure(new Location(Bukkit.getWorlds().get(1), loc.getX() - 16, loc.getY(), loc.getZ() - 16), false);
        if (ran.nextBoolean())
            generateRandomStructure(new Location(Bukkit.getWorlds().get(1), loc.getX() + 16, loc.getY(), loc.getZ()), false);
        if (ran.nextBoolean())
            generateRandomStructure(new Location(Bukkit.getWorlds().get(1), loc.getX() - 16, loc.getY(), loc.getZ()), false);
        if (ran.nextBoolean())
            generateRandomStructure(new Location(Bukkit.getWorlds().get(1), loc.getX() + 16, loc.getY(), loc.getZ() + 16), false);
        if (ran.nextBoolean())
            generateRandomStructure(new Location(Bukkit.getWorlds().get(1), loc.getX(), loc.getY(), loc.getZ() + 16), false);
        if (ran.nextBoolean())
            generateRandomStructure(new Location(Bukkit.getWorlds().get(1), loc.getX() - 16, loc.getY(), loc.getZ() + 16), false);

    }


    private void generateRandomStructure(Location loc, boolean valid) {

        Location genLoc;
        if (valid)
            genLoc = loc;
        else
            genLoc = getValidCageloc(loc);

        if (genLoc == null)
            return;

        StructureLoader.Rotation rotation = Arrays.asList(StructureLoader.Rotation.values()).get(ran.nextInt(4));

        // Is this cage a kitchen?
        boolean isCageKitchen = ran.nextInt(3) == 0;

        // If isKitchen generate a kitchen
        if (isCageKitchen) {

            if (ran.nextBoolean())
                StructureLoader.generateStructure(loc, "lochness", "lochness:cage.6", rotation);
            else
                StructureLoader.generateStructure(loc, "lochness", "lochness:cage.7", rotation);

            // Generate chestLootTable at adequate location
            switch (rotation) {
                case ZERO:
                    chestLoc.add(loc.getChunk().getBlock(7, loc.getBlockY() + 2, 2).getLocation());
                    break;
                case NINTY:
                    chestLoc.add(loc.getChunk().getBlock(13, loc.getBlockY() + 2, 7).getLocation());
                    break;
                case HUNDRED_EIGHTY:
                    //TODO: Fix problem witch chest loot tables
                    chestLoc.add(loc.getChunk().getBlock(8, loc.getBlockY() + 2, 13).getLocation());
                    break;
                case TWOHUNDRED_SEVENTY:
                    chestLoc.add(loc.getChunk().getBlock(2, loc.getBlockY() + 2, 8).getLocation());
                    break;
            }

        }

        // If is not Kitchen generate normal cage
        if(!isCageKitchen) {
            int cageNum = ran.nextInt(5) + 1;
            StructureLoader.generateStructure(loc, "lochness", "lochness:cage." + cageNum, rotation);
        }

        // Spawn up to 4 striders / drownds
        int mobNum = 0;
        if(isCageKitchen)
            mobNum = ran.nextInt(3) + 1;
        else
            mobNum = ran.nextInt(5)+1;

        for(int mobSpawnIteration = 0; mobSpawnIteration < mobNum; mobSpawnIteration++) {

            Location spawnLoc;

            switch (rotation) {
                case ZERO:
                    spawnLoc = loc.getChunk().getBlock(4, loc.getBlockY() + 4, 4).getLocation();
                    break;
                case NINTY:
                    spawnLoc = loc.getChunk().getBlock(11, loc.getBlockY() + 4, 4).getLocation();
                    break;
                case HUNDRED_EIGHTY:
                    spawnLoc = loc.getChunk().getBlock(11, loc.getBlockY() + 4, 11).getLocation();
                    break;
                case TWOHUNDRED_SEVENTY:
                    spawnLoc = loc.getChunk().getBlock(4, loc.getBlockY() + 4, 11).getLocation();
                    break;
                default:
                    spawnLoc = Bukkit.getWorlds().get(1).getSpawnLocation(); // Won't ever happen
            }
            spawnLoc.setX(spawnLoc.getX() + 0.1 * mobSpawnIteration);

            // Spawn striders
            if(isCageKitchen) {
                Drowned drowned = (Drowned) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.DROWNED);
                drowned.setRemoveWhenFarAway(false);
            }
            else {
                Strider strider = (Strider) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.STRIDER);
                strider.setRemoveWhenFarAway(false);
            }
        }

    }

    private Location getValidCageloc(Location loc) {

        Chunk chunk = loc.getBlock().getChunk();

        loc.setY(25);
        loc.setX(loc.getX() + 8);
        loc.setZ(loc.getZ() + 8);

        int bottom = 0;
        int top = 0;

        A:
        while(true) {

            while (loc.getBlock().getType() != Material.AIR) {
                loc.setY(loc.getY() + 1);
                if(loc.getBlockY() >= 255)
                    break A;
            }
            bottom = loc.getBlockY();

            while (loc.getBlock().getType() == Material.AIR) {
                loc.setY(loc.getY() + 1);
                if(loc.getBlockY() >= 255)
                    break A;
            }
            top = loc.getBlockY();

            if (top - bottom > 45)
                break A;
        }

        loc.setX(loc.getX() - 8);
        loc.setZ(loc.getZ() - 8);

        if (top - bottom < 45)
            return null;

        int heightOffset = ran.nextInt(11) - 5;
        loc.setY(loc.getY() - 25 + heightOffset);

        return loc;
    }

}

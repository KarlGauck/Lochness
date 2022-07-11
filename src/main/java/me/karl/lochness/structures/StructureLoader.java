package me.karl.lochness.structures;

import me.karl.lochness.Lochness;
import org.bukkit.*;
import org.bukkit.util.Vector;

public class StructureLoader {

    public static boolean areIslandsGenerated = false;
    public static boolean isCaveLoaded = false;

    public static void generateStructure(Location loc, String meta, String structureName) {
        generateStructure(loc, meta, structureName, Rotation.ZERO);
    }

    public static void generateStructure(Location loc, String meta, String structureName, Rotation rotation) {
        generateStructure(loc, meta, structureName, rotation, -1);
    }

    public static void generateStructure(Location loc, String meta, String structureName, Rotation rotation, int height) {
        if(loc.getBlockY() >= 255)
            return;

        String world = "";
        if(loc.getWorld().getEnvironment() == World.Environment.NORMAL)
            world = "minecraft:overworld";
        if(loc.getWorld().getEnvironment() == World.Environment.NETHER)
            world = "minecraft:the_nether";
        if(loc.getWorld().getEnvironment() == World.Environment.THE_END)
            world = "minecraft:the_end";

        String rotationString = "";
        switch (rotation) {
            case ZERO: rotationString = ",rotation:\"NONE\""; break;
            case NINTY: rotationString = ",rotation:\"CLOCKWISE_90\",posX:15"; break;
            case HUNDRED_EIGHTY: rotationString = ",rotation:\"CLOCKWISE_180\",posX:15,posZ:15"; break;
            case TWOHUNDRED_SEVENTY: rotationString = ",rotation:\"COUNTERCLOCKWISE_90\",posZ:15"; break;
        }

        String cmd = "execute in " + world + " run setblock " + loc.getBlockX()
                + " " + loc.getBlockY() + " " + loc.getBlockZ() + " minecraft:structure_block{metadata:\"" + meta +
                "\",powered:0b,mode:\"LOAD\",name:\"" + structureName + "\"" + rotationString + "}";

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);

        loc.setY(loc.getY() + 1);
        loc.getBlock().setType(Material.AIR);
        loc.getBlock().setType(Material.REDSTONE_BLOCK);

        loc.getBlock().setType(Material.AIR);
        loc.setY(loc.getY() - 1);
        loc.getBlock().setType(Material.AIR);

    }

    public enum Rotation {
        ZERO,
        NINTY,
        HUNDRED_EIGHTY,
        TWOHUNDRED_SEVENTY
    }

    // --------------------------------------------  CAVE GENERATION -----------------------------------------------------------//

    public static void generateCave() {
        for(int x = 0; x < 3; x++) {
            for(int y = 0; y < 2; y++) {
                for(int z = 0; z < 3; z++) {

                    Location loc = new Location(Bukkit.getWorlds().get(2), 400 + (48 * x), 5 + (48 * y), 0 + (48 * z));
                    for(int chunkX = 0; chunkX < 3; chunkX ++) {
                        for(int chunkZ = 0; chunkZ < 3; chunkZ ++) {
                            Location chunkLoc = loc.add(new Vector(16 * chunkX, 0, 16 * chunkZ));
                            chunkLoc.getChunk().load();
                        }
                    }

                    generateStructure(loc, "lochness", "lochness:" + x + "." + y + "." + z);

                }
            }
        }
    }

    //----------------------------------------  ISLAND GENERATION  -------------------------------------------------------------------//

    private static void generateIsland(Location loc) {
        loc.setY(10);
        generateStructure(loc, "lochness", "lochness:island.pillar");
        loc.setX(loc.getX() + 5);
        loc.setZ(loc.getZ() + 5);
        loc.setY(40);
        generateStructure(loc, "lochness", "lochness:island.1");
    }

    private static void generateGround(Location midPoint) {
        for(int x = 0; x < 2; x ++) {
            for(int z = 0; z < 2; z ++) {

                Location loc = midPoint.clone();
                loc.setX(midPoint.getX() - 40 + (40 * x));
                loc.setZ(midPoint.getZ() - 40 + (40 * z));
                loc.setY(29);

                generateStructure(loc, "lochness", "lochness:island.ground." + x + "." + z);

            }
        }
    }

    public static void pregenerateArea(Location loc, int area) {

        Location chunkLoc = loc.clone();
        chunkLoc.setX(loc.getX() - (area / 2) * 16);
        chunkLoc.setY(loc.getY() - (area / 2) * 16);

        for(int x = 0; x < area; x++) {
            for(int z = 0; z < area; z++) {
                chunkLoc.getChunk().load(true);
                chunkLoc.getChunk().setForceLoaded(true);
                chunkLoc.setX(chunkLoc.getX() + 16);
                chunkLoc.setX(chunkLoc.getZ() + 16);
            }
        }

    }

    public static void generateIslands() {

        //TODO: Tell player, if biome was not found

        Location loc = Lochness.getIslandLoc();

        Location islandLoc = loc.clone();
        islandLoc.setX(loc.getX() + 30 - 15);
        islandLoc.setZ(loc.getZ() - 15);
        generateIsland(islandLoc);

        islandLoc = loc.clone();
        islandLoc.setX(loc.getX() - 30 - 15);
        islandLoc.setZ(loc.getZ() - 15);
        generateIsland(islandLoc);

        islandLoc = loc.clone();
        islandLoc.setZ(loc.getZ() + 30 - 15);
        islandLoc.setX(loc.getX() - 15);
        generateIsland(islandLoc);

        islandLoc = loc.clone();
        islandLoc.setZ(loc.getZ() - 30 - 15);
        islandLoc.setX(loc.getX() - 15);
        generateIsland(islandLoc);

        generateGround(loc);
        Lochness.getIslandLoc().getBlock().setType(Material.LODESTONE);

        areIslandsGenerated = true;

    }

    public static void fixLoadingIssues() {
        Location poRLoc = Lochness.getIslandLoc().add(new Vector(8, 36, 5));
        String cmd = "fill " + poRLoc.getBlockX() + " " + poRLoc.getBlockY() + " " + poRLoc.getBlockZ() + " " +
                (poRLoc.getBlockX() + 30) + " " + (poRLoc.getBlockY() + 20) + " " + (poRLoc.getBlockZ() + 30);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),  cmd + " lantern[waterlogged=false] replace lantern[waterlogged=true]");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd + " stone_slab[waterlogged=false,type=top] replace stone_slab[waterlogged=true,type=top]");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd + " stone_slab[waterlogged=false,type=bottom] replace stone_slab[waterlogged=true,type=bottom]");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd + " andesite_wall[waterlogged=false] replace andesite_wall[waterlogged=true]");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd + " cobblestone_wall[waterlogged=false] replace cobblestone_wall[waterlogged=true]");

        Location blackDoor = Lochness.getIslandLoc().add(new Vector(17, 36, 23));
        blackDoor.getBlock().setType(Material.AIR);
        blackDoor.getBlock().setType(Material.REDSTONE_BLOCK);
        Location whiteDoor = Lochness.getIslandLoc().add(new Vector(17, 36, 22));
        whiteDoor.getBlock().setType(Material.AIR);
        whiteDoor.getBlock().setType(Material.EMERALD_BLOCK);
    }

}

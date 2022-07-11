package me.karl.lochness.structures;

import org.bukkit.*;
import org.bukkit.block.Biome;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BiomeFinder {

    private static final int MIN_DISTANCE_TO_LAND = 210;

    public static Location getBiomeLocation(Biome... biomes) {

        World world = Bukkit.getWorlds().get(0);
        Random ran = new Random();
        ran.setSeed(world.getSeed());

        int xRan = ran.nextInt(20001) - 10000;
        int zRan = ran.nextInt(20001) - 10000;

        Location biomeLoc = null;

        List biomeList = Arrays.asList(biomes);

        DistanceIteration:
        for(int i = 0; i < 100000; i++) {

            for(int offset = 0; offset < 5; offset ++) {

                int xOffset = 500 * (offset - 2) + xRan;
                int zOffset = 500 * (offset - 2) + zRan;

                // Test for location in direction of each axis

                int x = i * 32 + xOffset;
                int z = zOffset;
                if(biomeList.contains(world.getBiome(x, 0, z))) {
                    biomeLoc = new Location(world, x, 0, z);
                    if(isRegionValid(biomeLoc, biomeList, MIN_DISTANCE_TO_LAND))
                        break DistanceIteration;
                }

                x = xOffset;
                z = i * 32 + zOffset;
                if(biomeList.contains(world.getBiome(x, 0, z))) {
                    biomeLoc = new Location(world, x, 0, z);
                    if(isRegionValid(biomeLoc, biomeList, MIN_DISTANCE_TO_LAND))
                        break DistanceIteration;
                }

                x = -i * 32 + xOffset;
                z = zOffset;
                if(biomeList.contains(world.getBiome(x, 0, z))) {
                    biomeLoc = new Location(world, x, 0, z);
                    if(isRegionValid(biomeLoc, biomeList, MIN_DISTANCE_TO_LAND))
                        break DistanceIteration;
                }

                x = xOffset;
                z = -i * 32 + zOffset;
                if(biomeList.contains(world.getBiome(x, 0, z))) {
                    biomeLoc = new Location(world, x, 0, z);
                    if(isRegionValid(biomeLoc, biomeList, MIN_DISTANCE_TO_LAND))
                        break DistanceIteration;
                }

                // Test for location between axis

                x = i * 32 + xOffset;
                z = i * 32 + zOffset;
                if(biomeList.contains(world.getBiome(x, 0, z))) {
                    biomeLoc = new Location(world, x, 0, z);
                    if(isRegionValid(biomeLoc, biomeList, MIN_DISTANCE_TO_LAND))
                        break DistanceIteration;
                }

                x = -i * 32 + xOffset;
                z = -i * 32 + zOffset;
                if(biomeList.contains(world.getBiome(x, 0, z))) {
                    biomeLoc = new Location(world, x, 0, z);
                    if(isRegionValid(biomeLoc, biomeList, MIN_DISTANCE_TO_LAND))
                        break DistanceIteration;
                }

                x = -i * 32 + xOffset;
                z = i * 32 + zOffset;
                if(biomeList.contains(world.getBiome(x, 0, z))) {
                    biomeLoc = new Location(world, x, 0, z);
                    if(isRegionValid(biomeLoc, biomeList, MIN_DISTANCE_TO_LAND))
                        break DistanceIteration;
                }

                x = i * 32 + xOffset;
                z = -i * 32 + zOffset;
                if(biomeList.contains(world.getBiome(x, 0, z))) {
                    biomeLoc = new Location(world, x, 0, z);
                    if(isRegionValid(biomeLoc, biomeList, MIN_DISTANCE_TO_LAND))
                        break DistanceIteration;
                }

            }


        }

        return biomeLoc;

    }

    private static boolean isRegionValid(Location loc, List biomeList, int MIN_DISTANCE) {

        for(int distance = 10; distance < MIN_DISTANCE; distance+=10) {

            // Test for biomesize on axis

            Location sideLoc = loc.clone();
            sideLoc.setX(loc.getX() + distance);
            if (!biomeList.contains(sideLoc.getBlock().getBiome()))
                return false;

            sideLoc = loc.clone();
            sideLoc.setX(loc.getX() - distance);
            if (!biomeList.contains(sideLoc.getBlock().getBiome()))
                return false;

            sideLoc = loc.clone();
            sideLoc.setZ(loc.getZ() + distance);
            if (!biomeList.contains(sideLoc.getBlock().getBiome()))
                return false;

            sideLoc = loc.clone();
            sideLoc.setZ(loc.getZ() - distance);
            if (!biomeList.contains(sideLoc.getBlock().getBiome()))
                return false;

            // Test for biomesize between axis

            sideLoc = loc.clone();
            sideLoc.setX(loc.getX() + distance);
            sideLoc.setZ(loc.getZ() + distance);
            if (!biomeList.contains(sideLoc.getBlock().getBiome()))
                return false;

            sideLoc = loc.clone();
            sideLoc.setX(loc.getX() - distance);
            sideLoc.setZ(loc.getZ() + distance);
            if (!biomeList.contains(sideLoc.getBlock().getBiome()))
                return false;

            sideLoc = loc.clone();
            sideLoc.setX(loc.getX() + distance);
            sideLoc.setZ(loc.getZ() - distance);
            if (!biomeList.contains(sideLoc.getBlock().getBiome()))
                return false;

            sideLoc = loc.clone();
            sideLoc.setX(loc.getX() - distance);
            sideLoc.setZ(loc.getZ() - distance);
            if (!biomeList.contains(sideLoc.getBlock().getBiome()))
                return false;
        }

        return true;
    }

}

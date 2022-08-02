package me.karl.lochness.entities;

import me.karl.lochness.Lochness;
import me.karl.lochness.entities.ceberos.Cerberus;
import me.karl.lochness.entities.lochness.LochnessBoss;
import me.karl.lochness.entities.watermonsters.hai.LochnessHai;
import me.karl.lochness.entities.watermonsters.hammerhai.LochnessHammerhai;
import me.karl.lochness.entities.watermonsters.krabbe.LochnessKrabbe;
import me.karl.lochness.entities.watermonsters.kraken.LochnessKraken;
import me.karl.lochness.entities.watermonsters.narwal.LochnessNarwal;
import me.karl.lochness.entities.watermonsters.orca.LochnessOrca;
import me.karl.lochness.entities.watermonsters.piranha.LochnessPiranha;
import me.karl.lochness.entities.watermonsters.rochen.LochnessRochen;
import me.karl.lochness.entities.watermonsters.turtle.LochnessTurtle;
import me.karl.lochness.structures.cave.CaveLogic;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;

public abstract class LochnessEntity implements Serializable{

    private static ArrayList<LochnessEntity> entities = new ArrayList<>();
    private static HashMap<String, LochnessEntity> unloadedEntities = new HashMap<>();

    protected int chunkX, chunkZ;
    protected int world;

    protected transient int taskID;

    public LochnessEntity(int world, int chunkX, int chunkZ) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        Bukkit.getWorlds().get(world).getChunkAt(chunkX, chunkZ).load();
        taskID = Bukkit.getScheduler().runTaskTimer(Lochness.getPlugin(), tick(), 1, 1).getTaskId();
        entities.add(this);
    }

    public Runnable tick() {
        return () -> {

        };
    }

    abstract public void updateGlobalChunkPos();

    public void remove() {
        stop();
    }

    protected void stop() {
        Bukkit.getScheduler().cancelTask(taskID);
        entities.remove(this);
    }

    public static ArrayList<LochnessEntity> getEntities() {
        return (ArrayList<LochnessEntity>) entities.clone();
    }

    protected void save(){}

    public static void removeEntities(Class<? extends LochnessEntity> type) {
        for(int i = entities.size()-1; i>=0; i--) {
            LochnessEntity e = entities.get(i);
            if(type.isInstance(e)) {
                e.remove();
            }
        }
    }

    public static void saveEntities() {
        File entityPath = new File("./" + Bukkit.getWorlds().get(0).getName() + "/LochnessEntities");
        if(!entityPath.exists())
            entityPath.mkdir();

        ObjectOutputStream objectOutputStream;

        try {

            ArrayList<LochnessEntity> allEntities = entities;
            allEntities.addAll(unloadedEntities.values());

            for(LochnessEntity entity: allEntities) {

                entity.save();

                // ------ Name file like entity to be able to instanciate it afterwards ------
                String fileName = "";
                if(entity instanceof LochnessBoss)
                    fileName = "LochnessBoss";
                else if(entity instanceof Cerberus)
                    fileName = "Cerberus";
                else if(entity instanceof LochnessTurtle)
                    fileName = "Turtel";
                else if(entity instanceof LochnessRochen)
                    fileName = "Rochen";
                else if(entity instanceof LochnessPiranha)
                    fileName = "Piranha";
                else if(entity instanceof LochnessOrca)
                    fileName = "Orca";
                else if(entity instanceof LochnessNarwal)
                    fileName = "Narwal";
                else if(entity instanceof LochnessKraken)
                    fileName = "Krake";
                else if(entity instanceof LochnessKrabbe)
                    fileName = "Krabbe";
                else if(entity instanceof LochnessHammerhai)
                    fileName = "Hammerhai";
                else if(entity instanceof LochnessHai)
                    fileName = "Hai";

                FileOutputStream fileOutputStream = new FileOutputStream("./" + Bukkit.getWorlds().get(0).getName() + "/LochnessEntities/" + fileName
                        + entities.indexOf(entity) + ".txt");
                objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(entity);
                fileOutputStream.close();
                objectOutputStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadEntities() {
        File entityPath = new File("./" + Bukkit.getWorlds().get(0).getName() + "/LochnessEntities");
        if(!entityPath.exists())
            return;

        ObjectInputStream objectInputStream;

        try {

            for(File file: entityPath.listFiles()) {
                FileInputStream fileInputStream = new FileInputStream("./" + Bukkit.getWorlds().get(0).getName() + "/LochnessEntities/" + file.getName());
                objectInputStream = new ObjectInputStream(fileInputStream);

                LochnessEntity entity = (LochnessEntity) objectInputStream.readObject();
                Chunk chunk = Bukkit.getWorlds().get(entity.world).getChunkAt(entity.chunkX, entity.chunkZ);
                if (chunk.isLoaded() && chunk.isEntitiesLoaded()) {
                    // ------ instanciate entity based on name, given when saved ------
                    loadEntity(entity, file.getName());
                } else {
                    unloadedEntities.put(file.getName(), entity);
                }

                file.delete();
                fileInputStream.close();
                objectInputStream.close();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void loadEntity(LochnessEntity entity, String filename) {
        // ------ instanciate entity based on name, given when saved ------
        if(filename.contains("LochnessBoss"))
            new LochnessBoss((LochnessBoss) entity);
        else if(filename.contains("Cerberus"))
            new Cerberus((Cerberus) entity);
        else if(filename.contains("Turtel"))
            new LochnessTurtle((LochnessTurtle) entity);
        else if(filename.contains("Rochen"))
            new LochnessRochen((LochnessRochen) entity);
        else if(filename.contains("Piranha"))
            new LochnessPiranha((LochnessPiranha) entity);
        else if(filename.contains("Orca"))
            new LochnessOrca((LochnessOrca) entity);
        else if(filename.contains("Narwal"))
            new LochnessNarwal((LochnessNarwal) entity);
        else if(filename.contains("Krake"))
            new LochnessKraken((LochnessKraken) entity);
        else if(filename.contains("Krabbe"))
            new LochnessKrabbe((LochnessKrabbe) entity);
        else if(filename.contains("Hammerhai"))
            new LochnessHammerhai((LochnessHammerhai) entity);
        else if(filename.contains("Hai"))
            new LochnessHai((LochnessHai) entity);
    }

    public static void tryLoadUnloadedEntities() {
        Iterator i = unloadedEntities.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, LochnessEntity> entryPair = (Map.Entry<String, LochnessEntity>)i.next();
            String filename = entryPair.getKey();
            LochnessEntity entity = entryPair.getValue();
            Chunk chunk = Bukkit.getWorlds().get(entity.world).getChunkAt(entity.chunkX, entity.chunkZ);
            if (chunk.isLoaded() && chunk.isEntitiesLoaded()) {
                loadEntity(entity, filename);
                i.remove();
            }
        }
    }

    public static <T extends LochnessEntity> boolean isEntityAlive(Class<T> type) {
        for(LochnessEntity e: getEntities()) {
            if(type.isInstance(e))
                return true;
        }
        return false;
    }

    protected boolean isTargetValid(Player player, World world) {
        if (player == null)
            return false;
        if (!player.isOnline())
            return false;
        if (player.isDead())
            return false;
        if (!player.getWorld().equals(world))
            return false;
        return true;
    }

    public static void resetAllEntities() {
        CaveLogic.clearEntities();
        entities.clear();

        File entityPath = new File("./" + Bukkit.getWorlds().get(0).getName() + "/LochnessEntities");
        if(!entityPath.exists())
            return;
        for(File file: entityPath.listFiles())
            file.delete();

        CaveLogic.spawnEntities();
    }

}

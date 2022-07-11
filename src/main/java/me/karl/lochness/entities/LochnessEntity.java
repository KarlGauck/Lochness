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
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;

public class LochnessEntity implements Serializable{

    private static ArrayList<LochnessEntity> entities = new ArrayList<>();

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
        return new Runnable() {
            @Override
            public void run() {

            }
        };
    }

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

        ObjectOutputStream objectOutputStream = null;

        try {

            for(LochnessEntity entity: entities) {

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

        ObjectInputStream objectInputStream = null;

        try {

            for(File file: entityPath.listFiles()) {
                FileInputStream fileInputStream = new FileInputStream("./" + Bukkit.getWorlds().get(0).getName() + "/LochnessEntities/" + file.getName());
                objectInputStream = new ObjectInputStream(fileInputStream);

                // ------ instanciate entity based on name, given when saved ------
                if(file.getName().contains("LochnessBoss"))
                    new LochnessBoss((LochnessBoss) objectInputStream.readObject());
                else if(file.getName().contains("Cerberus"))
                    new Cerberus((Cerberus) objectInputStream.readObject());
                else if(file.getName().contains("Turtel"))
                    new LochnessTurtle((LochnessTurtle) objectInputStream.readObject());
                else if(file.getName().contains("Rochen"))
                    new LochnessRochen((LochnessRochen) objectInputStream.readObject());
                else if(file.getName().contains("Piranha"))
                    new LochnessPiranha((LochnessPiranha) objectInputStream.readObject());
                else if(file.getName().contains("Orca"))
                    new LochnessOrca((LochnessOrca) objectInputStream.readObject());
                else if(file.getName().contains("Narwal"))
                    new LochnessNarwal((LochnessNarwal) objectInputStream.readObject());
                else if(file.getName().contains("Krake"))
                    new LochnessKraken((LochnessKraken) objectInputStream.readObject());
                else if(file.getName().contains("Krabbe"))
                    new LochnessKrabbe((LochnessKrabbe) objectInputStream.readObject());
                else if(file.getName().contains("Hammerhai"))
                    new LochnessHammerhai((LochnessHammerhai) objectInputStream.readObject());
                else if(file.getName().contains("Hai"))
                    new LochnessHai((LochnessHai) objectInputStream.readObject());


                file.delete();
                fileInputStream.close();
                objectInputStream.close();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
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
        CaveLogic.resetEntities();
        entities.clear();

        File entityPath = new File("./" + Bukkit.getWorlds().get(0).getName() + "/LochnessEntities");
        if(!entityPath.exists())
            return;
        for(File file: entityPath.listFiles())
            file.delete();

        CaveLogic.spawnEntities();
    }

}

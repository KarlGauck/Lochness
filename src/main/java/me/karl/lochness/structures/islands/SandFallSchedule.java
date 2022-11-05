package me.karl.lochness.structures.islands;

import me.karl.lochness.Lochness;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class SandFallSchedule {

    public static Runnable sandFallScedule() {
        return () -> {

            Location fallTubeLoc = Lochness.getIslandLoc().add(new Vector(0, 50, 7));
            List<Entity> entityArrayList = Bukkit.getWorlds().get(0).getEntities();
            for(Entity entity: entityArrayList) {

                if(entity.getLocation().distance(fallTubeLoc) > 6)
                    continue;
                if(!(entity instanceof FallingBlock)) {
                    if(!(entity instanceof Player))
                        entity.teleport(entity.getLocation().add(new Vector(17, -5, 3)));
                    continue;
                }

                entity.remove();

            }

        };
    }

}

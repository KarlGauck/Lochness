package me.karl.lochness.entities;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class EntityLoadEvent implements Listener {

    @EventHandler
    public void onLoad(ChunkLoadEvent event) {
        LochnessEntity.tryLoadUnloadedEntities();
    }

    @EventHandler
    public void onUnload(ChunkUnloadEvent event) {
        for (LochnessEntity entity : LochnessEntity.getEntities()) {
            entity.updateGlobalChunkPos();
        }
    }

}

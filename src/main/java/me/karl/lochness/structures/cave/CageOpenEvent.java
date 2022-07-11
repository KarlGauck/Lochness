package me.karl.lochness.structures.cave;

import me.karl.lochness.Lochness;
import me.karl.lochness.structures.hadesroom.ItemFrameEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class CageOpenEvent implements Listener {

    @EventHandler
    public void onItemFrame(PlayerInteractAtEntityEvent event) {

        Entity entity = event.getRightClicked();
        ItemStack item = event.getPlayer().getEquipment().getItemInMainHand();

        if(!item.hasItemMeta())
            return;

        ItemMeta meta = item.getItemMeta();

        if(!meta.hasDisplayName())
            return;

        String displayName = meta.getDisplayName();
        Location center = null;

        if(!(entity instanceof ItemFrame))
            return;

        if(!meta.hasCustomModelData())
            return;

        if(displayName.length() < 10)
            return;
        if(entity.getScoreboardTags().contains("defense1") && displayName.substring(displayName.length()-10).equals("Defense #1")) {
            animate(new Location(Bukkit.getWorlds().get(2), 579, 42, 269), DIRECTION.NORTH);
        }
        if(entity.getScoreboardTags().contains("defense2") && displayName.substring(displayName.length()-10).equals("Defense #2")) {
            animate(new Location(Bukkit.getWorlds().get(2), 629, 51, 158), DIRECTION.SOUTH);
        }

        if(displayName.length() < 11)
            return;
        if(entity.getScoreboardTags().contains("strength1") && displayName.substring(displayName.length()-11).equals("Strength #1")) {
            animate(new Location(Bukkit.getWorlds().get(2), 574, 38, 269), DIRECTION.NORTH);
        }
        if(entity.getScoreboardTags().contains("strength2") && displayName.substring(displayName.length()-11).equals("Strength #2")) {
            animate(new Location(Bukkit.getWorlds().get(2), 583, 37, 271), DIRECTION.NORTH);
        }

        if(displayName.length() < 15)
            return;
        if(entity.getScoreboardTags().contains("regeneration1") && displayName.substring(displayName.length()-15).equals("Regeneration #1")) {
            animate(new Location(Bukkit.getWorlds().get(2), 563, 42, 178), DIRECTION.NORTH);
        }
        if(entity.getScoreboardTags().contains("regeneration2") && displayName.substring(displayName.length()-15).equals("Regeneration #2")) {
            animate(new Location(Bukkit.getWorlds().get(2), 644, 52, 155), DIRECTION.SOUTH);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if(block == null) {
            return;
        }
        if(!block.getType().equals(Material.CRIMSON_WALL_SIGN))
            return;

        if(block.getLocation().toVector().equals(new Vector(626, 54, 167))) {
            animate(new Location(Bukkit.getWorlds().get(2), 625, 54, 167), DIRECTION.EAST);
        }

        if(block.getLocation().toVector().equals(new Vector(588, 45, 169))) {
            animate(new Location(Bukkit.getWorlds().get(2), 588, 45, 170), DIRECTION.NORTH);
        }

        if(block.getLocation().toVector().equals(new Vector(595, 32, 159))) {
            animate(new Location(Bukkit.getWorlds().get(2), 596, 32, 159), DIRECTION.WEST);
        }
    }

    public void animate(Location center, DIRECTION dir) {
        Waterlogged data = (Waterlogged) Material.CRIMSON_FENCE.createBlockData();
        data.setWaterlogged(true);

        Bukkit.getScheduler().runTaskLater(Lochness.getPlugin(), new Runnable() {
            @Override
            public void run() {
                center.clone().add(new Vector(0, 1, 0)).getBlock().setType(Material.WATER);
                center.clone().add(new Vector(0, -1, 0)).getBlock().setType(Material.WATER);
                center.clone().add(dir.getPosUp1()).getBlock().setType(Material.CRIMSON_FENCE);
                center.clone().add(dir.getPosDown1()).getBlock().setType(Material.CRIMSON_FENCE);
                center.clone().add(dir.getPosDown1()).getBlock().setBlockData(data);
                center.clone().add(dir.getPosUp1()).getBlock().setBlockData(data);
            }
        }, 10);

        Bukkit.getScheduler().runTaskLater(Lochness.getPlugin(), new Runnable() {
            @Override
            public void run() {
                center.clone().add(dir.getPosUp1()).getBlock().setType(Material.WATER);
                center.clone().add(dir.getPosDown1()).getBlock().setType(Material.WATER);
                center.clone().add(dir.getPosUp2()).getBlock().setType(Material.CRIMSON_FENCE);
                center.clone().add(dir.getPosDown2()).getBlock().setType(Material.CRIMSON_FENCE);
                center.clone().add(dir.getPosDown2()).getBlock().setBlockData(data);
                center.clone().add(dir.getPosUp2()).getBlock().setBlockData(data);
            }
        }, 20);

        Bukkit.getScheduler().runTaskLater(Lochness.getPlugin(), new Runnable() {
            @Override
            public void run() {
                center.clone().add(dir.getPosUp2()).getBlock().setType(Material.WATER);
                center.clone().add(dir.getPosDown2()).getBlock().setType(Material.WATER);
                center.clone().add(dir.getPosUp3()).getBlock().setType(Material.CRIMSON_FENCE);
                center.clone().add(dir.getPosDown3()).getBlock().setType(Material.CRIMSON_FENCE);
                center.clone().add(dir.getPosDown3()).getBlock().setBlockData(data);
                center.clone().add(dir.getPosUp3()).getBlock().setBlockData(data);
            }
        }, 30);

        Bukkit.getScheduler().runTaskLater(Lochness.getPlugin(), new Runnable() {
            @Override
            public void run() {
                center.clone().add(dir.getPosUp3()).getBlock().setType(Material.WATER);
                center.clone().add(dir.getPosDown3()).getBlock().setType(Material.WATER);
                center.clone().add(dir.getPosUp4()).getBlock().setType(Material.CRIMSON_FENCE);
                center.clone().add(dir.getPosDown4()).getBlock().setType(Material.CRIMSON_FENCE);
                center.clone().add(dir.getPosDown4()).getBlock().setBlockData(data);
                center.clone().add(dir.getPosUp4()).getBlock().setBlockData(data);
            }
        }, 40);

        Bukkit.getScheduler().runTaskLater(Lochness.getPlugin(), new Runnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in minecraft:the_end run fill "
                        + (center.getBlockX() - 1) + " " + (center.getBlockY() - 1) + " " + (center.getBlockZ() - 1) + " "
                        + (center.getBlockX() + 1) + " " + (center.getBlockY() + 1) + " " + (center.getBlockZ() + 1) + " water replace crimson_wall_sign"
                );
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in minecraft:the_end run fill "
                        + (center.getBlockX() - 1) + " " + (center.getBlockY() - 1) + " " + (center.getBlockZ() - 1) + " "
                        + (center.getBlockX() + 1) + " " + (center.getBlockY() + 1) + " " + (center.getBlockZ() + 1) + " water replace iron_bars"
                );
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in minecraft:the_end run fill "
                        + (center.getBlockX() - 1) + " " + (center.getBlockY() - 1) + " " + (center.getBlockZ() - 1) + " "
                        + (center.getBlockX() + 1) + " " + (center.getBlockY() + 1) + " " + (center.getBlockZ() + 1) + " water replace chiseled_stone_bricks"
                );
                center.clone().add(dir.getPosUp4()).getBlock().setType(Material.WATER);
                center.clone().add(dir.getPosDown4()).getBlock().setType(Material.WATER);
                center.clone().getBlock().setType(Material.WATER);
            }
        }, 50);
    }

    public static enum DIRECTION {
        WEST(
                new Vector(0, 1, 1),
                new Vector(0, 0, 1),
                new Vector(0, -1, 1),
                new Vector(0, -1, 0),
                new Vector(0, -1, -1),
                new Vector(0, 0, -1),
                new Vector(0, 1, -1),
                new Vector(0, 1, 0)
        ),
        NORTH(
                new Vector(-1, 1, 0),
                new Vector(-1, 0, 0),
                new Vector(-1, -1, 0),
                new Vector(0, -1, 0),
                new Vector(1, -1, 0),
                new Vector(1, 0, 0),
                new Vector(1, 1, 0),
                new Vector(0, 1, 0)
        ),
        EAST(
                new Vector(0, 1, -1),
                new Vector(0, 0, -1),
                new Vector(0, -1, -1),
                new Vector(0, -1, 0),
                new Vector(0, -1, 1),
                new Vector(0, 0, 1),
                new Vector(0, 1, 1),
                new Vector(0, 1, 0)
        ),
        SOUTH(
                new Vector(1, 1, 0),
                new Vector(1, 0, 0),
                new Vector(1, -1, 0),
                new Vector(0, -1, 0),
                new Vector(-1, -1, 0),
                new Vector(-1, 0, 0),
                new Vector(-1, 1, 0),
                new Vector(0, 1, 0)
        );

        public Vector posUp1;
        public Vector posUp2;
        public Vector posUp3;
        public Vector posUp4;
        public Vector posDown1;
        public Vector posDown2;
        public Vector posDown3;
        public Vector posDown4;

        DIRECTION(Vector posDown1, Vector posDown2, Vector posDown3, Vector posDown4, Vector posUp1, Vector posUp2, Vector posUp3, Vector posUp4) {
            this.posDown1 = posDown1;
            this.posDown2 = posDown2;
            this.posDown3 = posDown3;
            this.posDown4 = posDown4;
            this.posUp1 = posUp1;
            this.posUp2 = posUp2;
            this.posUp3 = posUp3;
            this.posUp4 = posUp4;
        }

        public Vector getPosDown1() {
            return posDown1;
        }

        public Vector getPosDown2() {
            return posDown2;
        }

        public Vector getPosDown3() {
            return posDown3;
        }

        public Vector getPosDown4() {
            return posDown4;
        }

        public Vector getPosUp1() {
            return posUp1;
        }

        public Vector getPosUp2() {
            return posUp2;
        }

        public Vector getPosUp3() {
            return posUp3;
        }

        public Vector getPosUp4() {
            return posUp4;
        }
    }

}

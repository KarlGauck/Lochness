package me.karl.lochness.structures.hadesroom;

import me.karl.lochness.PluginUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class ItemFrameEvent implements Listener {

    private static final Material KEY_MATERIAL = Material.IRON_NUGGET;
    private static Location dropLoc;

    private static boolean[] key = new boolean[5];
    static{
        for(int k = 0; k < key.length; k++)
            key[k] = false;
    }

    @EventHandler
    public void onHangingPlace(PlayerInteractEntityEvent event) {

        Entity entity = event.getRightClicked();
        if(!(entity instanceof ItemFrame))
            return;

        int key = 0;
        boolean hasScoreboardTag = false;
        for(int i = 1; i <= 5; i++) {
            if(!entity.getScoreboardTags().contains("key" + i))
                continue;

            if(i == 5) {
                dropLoc = entity.getLocation().add(new Vector(2, 0, 0));
            }

            hasScoreboardTag = true;
            key = i;
            break;
        }

        if(!hasScoreboardTag)
            return;

        ItemFrame itemframe = (ItemFrame) entity;
        ItemStack itemStack = event.getPlayer().getEquipment().getItemInMainHand();
        if(itemStack.getType() == Material.AIR)
            itemStack = event.getPlayer().getEquipment().getItemInOffHand();

        Location loc = itemframe.getLocation();
        loc.setZ(loc.getZ() - 1);

        if(itemStack.getType() != KEY_MATERIAL)
            return;

        ItemMeta meta = itemStack.getItemMeta();
        String string = meta.getDisplayName();
        if(string.length() < 6)
            return;
        if(!string.substring(string.length()-6, string.length()).equals("Key #" + key))
            return;

        if(meta.getCustomModelData() != key)
            return;

        PluginUtils.grantAdvancement(event.getPlayer(), "lochness:sleuth");
        lit(itemframe, true);
        ItemFrameEvent.key[key-1] = true;

        if(isActive()) {
            ItemStack keyItem = new ItemStack(Material.GOLD_INGOT);
            ItemMeta keyItemMeta = keyItem.getItemMeta();
            keyItemMeta.setCustomModelData(1);
            keyItemMeta.setDisplayName("loot key");
            keyItem.setItemMeta(keyItemMeta);
            Bukkit.getWorlds().get(1).dropItem(dropLoc, keyItem);
        }

    }

    @EventHandler
    public void onRemove(EntityDamageEvent event) {

        Entity entity = event.getEntity();
        if(!(entity instanceof ItemFrame))
            return;

        Location loc = entity.getLocation();
        loc.setZ(loc.getZ() - 1);

        int key = 0;
        boolean hasScoreboardTag = false;
        for(int i = 1; i <= 5; i++) {
            if(!entity.getScoreboardTags().contains("key" + i))
                continue;

            hasScoreboardTag = true;
            key = i;
            break;
        }

        if(!hasScoreboardTag)
            return;

        lit(entity, false);
        ItemFrameEvent.key[key-1] = false;

    }

    private void lit(Entity entity, boolean lit) {
        if(!(entity instanceof ItemFrame))
            return;

        Location loc = entity.getLocation();
        loc.setZ(loc.getZ() - 2);

        String block = "";
        if(lit)
            block = "minecraft:redstone_block";
        else
            block = "minecraft:emerald_block";

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in minecraft:the_nether run setblock " + loc.getBlockX() + " " + loc.getBlockY() +
                " " + loc.getBlockZ() + " " + block);
    }

    private boolean isActive() {
        boolean isActive = true;
        for(int i = 0; i < 5; i++) {
            if(!key[i])
                isActive = false;
        }
        return isActive;
    }

}

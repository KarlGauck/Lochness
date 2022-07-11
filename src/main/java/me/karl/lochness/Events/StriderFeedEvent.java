package me.karl.lochness.Events;

import me.karl.lochness.Lochness;
import me.karl.lochness.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;

public class StriderFeedEvent implements Listener {

    public static ArrayList<UUID> striders = new ArrayList<>();
    public static ArrayList<Integer> striderTime = new ArrayList<>();

    @EventHandler
    public void onFeed(PlayerInteractEntityEvent event) {

        STRIDER_FEED:
        {
            Entity entity = event.getRightClicked();
            Player player = event.getPlayer();
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            ItemMeta itemMeta = itemStack.getItemMeta();

            if(itemStack.getType() != Material.STRUCTURE_BLOCK)
                break STRIDER_FEED;
            if(entity.getType() != EntityType.STRIDER)
                break STRIDER_FEED;
            if(itemMeta.getCustomModelData() != 1)
                break STRIDER_FEED;
            if(entity.getScoreboardTags().contains("feeded")) {
                break STRIDER_FEED;
            }

            striders.add(entity.getUniqueId());
            striderTime.add(30);
            entity.getWorld().spawnParticle(Particle.HEART, entity.getLocation().clone().add(new Vector(0, 1.3, 0)), 10, 0.4, 0.2, 0.4, 0.02);

            entity.addScoreboardTag("feeded");

            itemStack.setAmount(itemStack.getAmount() - 1);

            PluginUtils.grantAdvancement(player, "lochness:smells_tasty");
        }


    }

    public static Runnable getStriderLoop() {
        return new Runnable() {
            @Override
            public void run() {

                for(int striderIndex = striders.size()-1; striderIndex >= 0; striderIndex--) {
                    if(striderTime.get(striderIndex) < 0) {
                        removeStrider(striderIndex);
                        continue;
                    }

                    Strider strider = (Strider) Bukkit.getEntity(striders.get(striderIndex));
                    if(strider.isDead()) {
                        removeStrider(striderIndex);
                        continue;
                    }

                    Vector roomMiddle = Lochness.getRoomLoc().toVector();
                    roomMiddle.add(new Vector(20, 0, 20));
                    Vector velocity = strider.getLocation().toVector().subtract(roomMiddle);

                    velocity.normalize();
                    velocity.setY(0);

                    strider.setVelocity(velocity.multiply(-0.2));

                    striderTime.set(striderIndex, striderTime.get(striderIndex)-1);
                }

            }
        };
    }

    public static void removeStrider(int striderIndex) {
        striders.remove(striderIndex);
        striderTime.remove(striderIndex);
    }

}

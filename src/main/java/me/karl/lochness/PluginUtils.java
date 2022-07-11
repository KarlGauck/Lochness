package me.karl.lochness;

import me.karl.lochness.structures.cave.CaveLogic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class PluginUtils {

    public static EulerAngle convertVectorToEulerAngle(Location loc1, Location loc2) {

        Location loc1clone = loc1.clone();
        loc1clone.setY(0);
        Location loc2clone = loc2.clone();
        loc2clone.setY(0);
        return new EulerAngle(-Math.sin((loc2.getY() - loc1.getY()) / (loc1clone.distance(loc2clone))), 0f, 0f);

    }

    public static void damadgePlayer(Player player, double damadge, String entityName) {
        double finalDamadge = damadge;

        double armorPoints = player.getAttribute(Attribute.GENERIC_ARMOR).getValue();
        double armorReduction = 1 - (((double)(new Random().nextInt(1000)+1000) / 100000) + (2000 / 100000)) * armorPoints;
        finalDamadge = finalDamadge * armorReduction;

        EntityEquipment equipment = player.getEquipment();
        ItemStack helmet = equipment.getHelmet();
        ItemStack chestplate = equipment.getChestplate();
        ItemStack leggins = equipment.getLeggings();
        ItemStack boots = equipment.getBoots();

        double enchantmentProtection = 0;
        if(helmet != null)
            enchantmentProtection += helmet.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) * 0.04;
        if(chestplate != null)
            enchantmentProtection += chestplate.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) * 0.04;
        if(leggins != null)
            enchantmentProtection += leggins.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) * 0.04;
        if(boots != null)
            enchantmentProtection += boots.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) * 0.04;

        finalDamadge = finalDamadge * (1 - enchantmentProtection);

        Pig pig = (Pig) Bukkit.getWorlds().get(0).spawnEntity(new Location(Bukkit.getWorlds().get(0), 0, -1000, 0), EntityType.PIG);
        pig.setCustomName(entityName);
        player.damage(finalDamadge, pig);
        pig.remove();

    }

    public static double calculateDamadge(ItemStack stack, Player player) {
        double damadge = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
        damadge = damadge + 0.5 * (stack.getEnchantmentLevel(Enchantment.DAMAGE_ALL) + 1);
        return damadge;
    }

    public static void grantAdvancement(Player player, String namespacedKey) {
        NamespacedKey key = NamespacedKey.fromString(namespacedKey);
        AdvancementProgress progress = player.getAdvancementProgress(Bukkit.getAdvancement(key));
        for(String criteria : progress.getRemainingCriteria())
            progress.awardCriteria(criteria);
    }

}
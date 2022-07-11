package me.karl.lochness.enchantments.poseidonspower;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EnchantmentWrapper extends Enchantment {

    private int maxLvl;
    private String name;
    private String namespace;

    public EnchantmentWrapper(String namespace, String name, int maxLvl) {
        super(NamespacedKey.minecraft(namespace));
        this.name = name;
        this.maxLvl = maxLvl;
        this.namespace = namespace;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMaxLevel() {
        return maxLvl;
    }

    @Override
    public int getStartLevel() {
        return 0;
    }

    @NotNull
    @Override
    public EnchantmentTarget getItemTarget() {
        return null;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return false;
    }
}

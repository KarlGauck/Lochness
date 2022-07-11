package me.karl.lochness.enchantments.poseidonspower;

import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CustomEnchants {

    public static final Enchantment POSEIDONS_POWER = new EnchantmentWrapper("poseidons_power", "Poseidons Power", 1);

    public static void register() {
        boolean registered = Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(POSEIDONS_POWER);

        if(!registered)
            registerEnchantment(POSEIDONS_POWER);
    }

    public static void registerEnchantment(Enchantment enchantment) {
        boolean registered = true;
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(enchantment);
        }catch(Exception e) {
            registered = false;
            e.printStackTrace();
        }
        if(registered) {
            // send message ?!
        }
    }

}

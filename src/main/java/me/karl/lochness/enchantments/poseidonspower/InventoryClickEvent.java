package me.karl.lochness.enchantments.poseidonspower;

import me.karl.lochness.PluginUtils;
import me.karl.lochness.enchantments.poseidonspower.CustomEnchants;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InventoryClickEvent implements Listener {

    @EventHandler
    public void onInventoryInsert(org.bukkit.event.inventory.InventoryClickEvent event) {

        if(event.getView().getType() != InventoryType.ANVIL)
            return;
        AnvilInventory anvilInv = (AnvilInventory) event.getView().getTopInventory();


        Inventory inv = event.getClickedInventory();

        int amount = 0;
        switch(event.getClick()) {
            case SHIFT_RIGHT:
            case SHIFT_LEFT:{

                if(event.getClickedInventory().getType() == InventoryType.ANVIL)
                    return;

                ItemStack slotItem = event.getClickedInventory().getItem(event.getSlot());
                if(event.getSlot() == 2) {
                    if(slotItem.containsEnchantment(CustomEnchants.POSEIDONS_POWER)) {
                        PluginUtils.grantAdvancement((Player)event.getWhoClicked(), "lochness:the_strongest_weapon");
                    }
                }

                ItemStack firstStack = anvilInv.getItem(0);
                ItemStack secondStack = anvilInv.getItem(1);

                if(slotItem == null)
                    return;

                if(firstStack != null && firstStack.getType() == Material.TRIDENT) {
                    if(slotItem.getType() != Material.ENCHANTED_BOOK)
                        return;
                    if(!slotItem.getItemMeta().hasEnchant(CustomEnchants.POSEIDONS_POWER))
                        return;
                    amount = Math.min(anvilInv.getItem(0).getAmount(), inv.getItem(event.getSlot()).getAmount());
                } else if (secondStack != null && secondStack.getType() == Material.ENCHANTED_BOOK && secondStack.getItemMeta().hasEnchant(CustomEnchants.POSEIDONS_POWER)) {
                    if(slotItem.getType() != Material.TRIDENT)
                        return;
                    amount = Math.min(anvilInv.getItem(1).getAmount(), inv.getItem(event.getSlot()).getAmount());
                } else return;

                break;
            }
            case LEFT:
            case RIGHT: {

                ItemStack firstStack = anvilInv.getItem(0);
                ItemStack secondStack = anvilInv.getItem(1);
                if(event.getSlot() == 0 && secondStack == null)
                    return;
                if(event.getSlot() == 0 && secondStack.getType() != Material.ENCHANTED_BOOK)
                    return;
                if(event.getSlot() == 0 && !secondStack.getItemMeta().hasEnchant(CustomEnchants.POSEIDONS_POWER))
                    return;
                if(event.getSlot() == 1 && firstStack == null)
                    return;
                if(event.getSlot() == 1 && firstStack.getType() != Material.TRIDENT)
                    return;
                if(event.getSlot() != 0 && event.getSlot() != 1)
                    return;

                ItemStack cursorStack = ((Player)event.getWhoClicked()).getItemOnCursor();
                if(event.getSlot() == 0 && cursorStack.getType() != Material.TRIDENT)
                    return;
                if(event.getSlot() == 1 && cursorStack.getType() != Material.ENCHANTED_BOOK)
                    return;
                if(event.getSlot() == 1 && !cursorStack.getItemMeta().hasEnchant(CustomEnchants.POSEIDONS_POWER))
                    return;

                if(event.getSlot() == 0)
                    amount = Math.min(anvilInv.getItem(1).getAmount(), cursorStack.getAmount());
                if(event.getSlot() == 1)
                    amount = Math.min(anvilInv.getItem(0).getAmount(), cursorStack.getAmount());

                break;
            }
            default: return;
        }

        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("Lochness"), setResult(anvilInv, amount), 1);

    }

    @EventHandler
    public void onInventoryOuttake(org.bukkit.event.inventory.InventoryClickEvent event) {

        if(event.getView().getType() != InventoryType.ANVIL)
            return;
        if(event.getSlot() != 2)
            return;
        AnvilInventory anvilInv = (AnvilInventory) event.getView().getTopInventory();
        if(anvilInv.getItem(0) == null)
            return;
        if(anvilInv.getItem(0).getType() != Material.TRIDENT)
            return;
        if(anvilInv.getItem(1) == null)
            return;
        if(anvilInv.getItem(1).getType() != Material.ENCHANTED_BOOK)
            return;
        ItemStack enchantedBook = anvilInv.getItem(1);
        if(!enchantedBook.getItemMeta().hasEnchant(CustomEnchants.POSEIDONS_POWER))
            return;

        int amount = anvilInv.getItem(2).getAmount();
        Inventory inv = event.getClickedInventory();
        ((Player)event.getWhoClicked()).setItemOnCursor(getFinalTrident(inv.getItem(0), inv.getItem(1)));
        PluginUtils.grantAdvancement((Player)event.getWhoClicked(), "lochness:the_strongest_weapon");
        inv.getItem(0).setAmount(inv.getItem(0).getAmount()-amount);
        inv.getItem(1).setAmount(inv.getItem(1).getAmount()-amount);
        inv.getItem(2).setAmount(inv.getItem(2).getAmount()-amount);

    }

    private Runnable setResult(AnvilInventory inv, int amount) {
        return new Runnable() {
            @Override
            public void run() {
                inv.setItem(2, getFinalTrident(inv.getItem(0), inv.getItem(1)));
            }
        };
    }

    private ItemStack getFinalTrident(ItemStack trident, ItemStack book) {

        ItemStack finalTrident = trident.clone();
        ItemMeta tridentMeta = finalTrident.getItemMeta();
        tridentMeta.addEnchant(CustomEnchants.POSEIDONS_POWER, 1, false);
        List<String> lore = new ArrayList<>();
        if(tridentMeta.getLore() != null)
            lore = tridentMeta.getLore();
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Poseidons Power");
        tridentMeta.setLore(lore);
        finalTrident.setItemMeta(tridentMeta);

        return finalTrident;

    }

}

package me.karl.lochness.structures.endcredits;

import me.karl.lochness.Lochness;
import org.bukkit.*;
import org.bukkit.block.data.type.StructureBlock;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MessageHandler {

    public static Location teleportLocation = Lochness.getIslandLoc().add(new Vector(-16, 35, 13));
    public static ArmorStand armorStand;

    public static int MESSAGE_DELAY = 120;

    public static String[] messages = {
            ChatColor.BLUE + "[Faincraft]" + ChatColor.WHITE +  "  wow, hast du diesen Kampf gesehen?",
            ChatColor.DARK_RED + "[Karl]" + ChatColor.WHITE +  " Ja, das war unglaublich!",
            ChatColor.BLUE + "[Faincraft]" + ChatColor.WHITE +  " Ich dachte, Lochness zu besiegen wäre unmöglich",
            ChatColor.DARK_RED + "[Karl]" + ChatColor.WHITE +  " <p> haben das sehr gut gemacht",
            ChatColor.BLUE + "[Faincraft]" + ChatColor.WHITE +  " Es war auch eine sehr kooperative Aufgabe, sie mussten stark zusammen arbeiten",
            ChatColor.DARK_RED + "[Karl]" + ChatColor.WHITE +  " Dafür, dass ihr so mutig und stark gekämpft habt, überreichen wir euch hier ein kleines Andenken",
            ChatColor.BLUE + "[Faincraft]" + ChatColor.WHITE +  " Habt ihr Lust auf einen weiteren Endboss?",
            ChatColor.DARK_RED + "[Karl]" + ChatColor.WHITE +  " Wenn ja, könnt ihr auf jeden Fall beim Angry-Villager vorbeischauen, um eure Fähigkeiten ein weiteres mal auf die Probe zu stellen",
            ChatColor.BLUE + "[Faincraft]" + ChatColor.WHITE +  " Wenn ihr Lust auf einen neuen Endboss habt, alle Ideen und Vorschläge sind willkommen",
            ChatColor.DARK_RED + "[Karl]" + ChatColor.WHITE +  " Ich hoffe, das Plugin und der Endkampf haben euch gefallen",
            ChatColor.BLUE + "[Faincraft]" + ChatColor.WHITE +  " Wir würden uns auf jeden Fall über Feedback freuen",
            ChatColor.DARK_RED + "[Karl]" + ChatColor.WHITE +  " wenn ihr uns Feedback geben wollt, oder Ideen für einen dritten Endboss habt, könnt ihr sie gerne unter unserem Vorstellungsvideo in die Kommentare schreiben",
            ChatColor.BLUE + "[Faincraft]" + ChatColor.WHITE +  " Vielen Dank fürs Durchspielen, auf Wiedersehen"
    };

    public static void startEndcredits() {
        Lochness.getIslandLoc().add(new Vector(-14, 66, 8)).getBlock().setType(Material.AIR);
        Lochness.getIslandLoc().add(new Vector(-13, 67, 8)).getBlock().setType(Material.AIR);

        ArrayList<Player> players = new ArrayList<>();
        players = (ArrayList<Player>) Bukkit.getWorlds().get(2).getPlayers();
        for(Player p: Bukkit.getWorlds().get(2).getPlayers()) {
            p.teleport(teleportLocation);
        }
        displayMessages(players);
    }

    public static void displayMessages(List<Player> players) {

        String playerNames = "";
        for(int pInd = 0; pInd < players.size(); pInd++) {
            if(players.size() > 1) {
                if(pInd == players.size()-1) {
                    playerNames += " und " + players.get(pInd).getName();
                } else if(pInd != 0){
                    playerNames += ", " + players.get(pInd).getName();
                } else {
                    playerNames += players.get(pInd).getName();
                }
            } else {
                playerNames = players.get(pInd).getName();
            }
        }

        for(int i = 0; i < messages.length; i++) {
            int finalI = i;
            String finalPlayerNames = playerNames;
            int finalI1 = i;
            Bukkit.getScheduler().runTaskLater(Lochness.getPlugin(), () -> {
                for(Player p: Bukkit.getWorlds().get(0).getPlayers()) {
                    p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1, 1);
                }
                displayMessage(messages[finalI], finalPlayerNames);
                if(finalI1 == 5) {
                    Location loc = teleportLocation.clone().add(new Vector(-5.5, 1.5, 1));
                    loc.getWorld().spawnParticle(Particle.FLASH, loc, 1, 0, 0, 0, 0);
                    for(Player p: Bukkit.getWorlds().get(0).getPlayers()) {
                        p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 1, 1);
                    }
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " run summon item ~ ~ ~ {Item:{id:\"minecraft:player_head\",Count:1b,tag:{display:{Name:'{\"text\":\"Lochness Trophy\",\"color\":\"gold\",\"bold\":true,\"italic\":false}'},SkullOwner:{Id:[I;-1610321374,-73383112,-1786422944,436885721],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzMwOWJjN2Q5OWM0ODBhNWNkNjEzMzI5NGFjOWFhY2IwYTU5Njc4MDAyMzY3ODk1NGZlNjJkZTNjNWRkMzJhYyJ9fX0=\"}]}}}}}");
                }
            }, i * MESSAGE_DELAY);
        }

    }

    public static void displayMessage(String msg, String playerNames) {
        for(Player p: Bukkit.getWorlds().get(0).getPlayers()) {
            String message = msg;
            message = message.replace("<p>", playerNames);
            p.sendMessage("");
            p.sendMessage(message);
        }
    }


}

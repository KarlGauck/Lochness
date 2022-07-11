package me.karl.lochness;

import me.karl.lochness.structures.cave.CaveLogic;
import me.karl.lochness.structures.cave.InteractionEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;

public class SoundClass {

    public static long time = 0;
    public static int songTime = 1;
    public static int song = 0;

    public static String[] sounds = {
            "minecraft:music_disc.11",
            "minecraft:music_disc.13",
            "minecraft:music_disc.cat",
            "minecraft:music_disc.chirp",
            "minecraft:music_disc.far",
            "minecraft:music_disc.mall"
    };

    public static int[] soundTime = {
            3240,
            3040,
            2900,
            2820,
            2700,
            2820
    };

    public static void soundLoop() {
        if (InteractionEvent.doorLoc.getBlock().getType() == Material.IRON_BARS)
            return;

        if(time % (songTime) == 0) {
            playNextSong();
            time = 0;
        }

        time ++;
    }

    public static void playNextSong() {

        for(Player p: Bukkit.getOnlinePlayers()) {
            Location loc = p.getLocation();

            if (loc.getBlockX() < 544 || loc.getBlockX() > 544 + (48 * 3))
                continue;

            if (loc.getBlockZ() < 144 || loc.getBlockZ() > 144 + (48 * 3))
                continue;

            if(p.getScoreboardTags().contains("music=false"))
                continue;

            if(!hasVolume(p)) {
                p.addScoreboardTag("music_volume=1.0f");
            }


            int index = song++;
            if(song >= sounds.length)
                song = 0;

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + p.getName() + " at @s run playsound " + sounds[index] + " master @s ~ ~ ~ " + getVolume(p) + " 1");
            songTime = soundTime[index];
        }

    }

    public static boolean hasVolume(Player p) {
        boolean hasVolume = false;
        for(String s: p.getScoreboardTags()) {
            if(s.contains("music_volume=")) {
                hasVolume = true;
            }
        }
        return hasVolume;
    }

    public static float getVolume(Player p) {
        for(String s: p.getScoreboardTags()) {
            if(!s.contains("music_volume="))
                continue;

            float volume = 1.0f;
            try {
                volume = Float.valueOf(s.substring(s.indexOf('=') + 1));
            } catch (Exception e) {
                p.sendMessage("no matching volume found: " + (s.substring(s.indexOf('='))));
            }
            return volume;
        }
        return 1.0f;
    }

}

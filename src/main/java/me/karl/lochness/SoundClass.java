package me.karl.lochness;

import me.karl.lochness.structures.cave.InteractionEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class SoundClass {

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
        if (!isSoundActivated())
            return;

        playerLoop:
        for (Player p : Bukkit.getWorlds().get(2).getPlayers()) {
            Location loc = p.getLocation();
            if (loc.getBlockX() < 544 || loc.getBlockX() > 544 + (48 * 3))
                return;
            if (loc.getBlockZ() < 144 || loc.getBlockZ() > 144 + (48 * 3))
                return;

            for (String s: p.getScoreboardTags())
                if (s.contains("music=false"))
                    continue playerLoop;

            int trackTime = soundTime[getTrack(p)];

            // Time < 0 lets classes like MusicCommand prime song by setting time to -1 using the primeSong() method
            if (getTime(p) >= trackTime || getTime(p) < 0) {
                setNextTrack(p);
                playTrack(p, sounds[getTrack(p)]);
            }

            setTime(p, getTime(p) + 1);
        }

    }

    public static void primeSong(Player p) {
        if (!isSoundActivated())
            return;
        setTime(p, -1);
    }

    public static void setNextTrack(Player p) {
        int track = getTrack(p);
        setTrack(p, track + 1);
        setTime(p, 0);
        if (getTrack(p) >= sounds.length) {
            setTrack(p, 0);
        }
    }

    public static void playTrack(Player player, String song) {
        if(player.getScoreboardTags().contains("music=false"))
            return;

        if(!hasVolume(player)) {
            player.addScoreboardTag("music_volume=1.0f");
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + player.getName() + " at @s run playsound " + song + " master @s ~ ~ ~ " + getVolume(player) + " 1");
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

    public static int getTrack(Player p) {
        for (String s: p.getScoreboardTags()) {
            if (s.contains("music_track=")) {
                return Integer.parseInt(s.replaceAll("music_track=", ""));
            }
        }
        p.addScoreboardTag("music_track=0");
        return 0;
    }
    public static void setTrack(Player p, int track) {
        ArrayList<String> removalTags = new ArrayList<>();
        for (String s : p.getScoreboardTags())
            if (s.contains("music_track="))
                removalTags.add(s);
        for (String s: removalTags)
            p.removeScoreboardTag(s);
        p.addScoreboardTag("music_track=" + track);
    }

    public static int getTime(Player p) {
        for (String s: p.getScoreboardTags()) {
            if (s.contains("music_time="))
                return Integer.parseInt(s.replaceAll("music_time=", ""));
        }
        p.addScoreboardTag("music_time=-1");
        return -1;
    }

    public static void setTime(Player p, int time) {
        ArrayList<String> removalTags = new ArrayList<>();
        for (String s: p.getScoreboardTags()) {
            if (s.contains("music_time="))
                removalTags.add(s);
        }
        for (String s: removalTags)
            p.removeScoreboardTag(s);
        p.addScoreboardTag("music_time=" + time);
    }

    public static Boolean isSoundActivated() {
        return InteractionEvent.doorLoc.getBlock().getType() != Material.IRON_BARS;
    }

}

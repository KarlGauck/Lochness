package me.karl.lochness.commandexecutor;

import me.karl.lochness.Lochness;
import me.karl.lochness.SoundClass;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MusicCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length < 1) {
            sender.sendMessage(Lochness.getPrefix() + "please use arguments");
            return false;
        }
        if(!(sender instanceof Player))
            return false;

        Player p = (Player) sender;
        if(args[0].equals("enable")) {
            p.removeScoreboardTag("music=false");
            p.addScoreboardTag("music=true");
        }
        if(args[0].equals("disable")) {
            p.removeScoreboardTag("music=true");
            p.addScoreboardTag("music=false");
            p.stopSound(SoundClass.sounds[SoundClass.song]);
        }
        if(args[0].equals("volume")) {
            if(args.length < 2) {
                p.sendMessage(Lochness.getPrefix() + "your volume is at " + SoundClass.getVolume(p) * 100 + "%");
                return false;
            }
            try {
                float volume = Float.parseFloat(args[1].replaceAll("%", "")) / 100;
                removeVolume(p);
                p.addScoreboardTag("music_volume=" + volume + "f");
                p.sendMessage(Lochness.getPrefix() + "volume is at " + volume);
            } catch (Exception e)  {
                p.sendMessage(Lochness.getPrefix() + "volume is not a float pls. use '0.5' for example");
            }
        }

        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if(args.length == 1) {
            list.add("enable");
            list.add("disable");
            list.add("volume");
        } else if(args.length == 2) {
            if(!args[0].equals("volume"))
                return list;

            list.add("<float>");
        }
        return list;
    }

    public static void removeVolume(Player p) {
        for(String s: p.getScoreboardTags()) {
            if(s.contains("music_volume=")) {
                p.removeScoreboardTag(s);
                return;
            }
        }
    }

}

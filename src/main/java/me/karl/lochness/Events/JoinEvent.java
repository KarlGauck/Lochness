package me.karl.lochness.Events;

import me.karl.lochness.Lochness;
import me.karl.lochness.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setResourcePack(Lochness.RESOURCEPACK_DOWNLOAD_LINK);
        player.sendMessage(Lochness.getPrefix() + "Use /guide to get a book full of helpful tips");



        if(!Lochness.isDatapackInstalled) {
            player.sendMessage(Lochness.getPrefix() + ChatColor.YELLOW + "----------  WARNING  ----------");
            player.sendMessage(ChatColor.YELLOW + "The automatic download of the required datapack didn't work. " +
                    "Please make sure, to download and install it manually into " +
                    "the world/datapack folder. (Make sure, the file is called \"lochness.zip\") " +
                    "After that, type \"/reload\" to reload your server and plugin. " +
                    "Also notice, that certain areas of your world can be replaced by " +
                    "structure generation");
            player.sendMessage("");
            player.sendMessage("Datapack Downloadlink: " + ChatColor.AQUA + Lochness.DATAPACK_DOWNLOAD_LINK);
        } else {
            Boolean isInstalled = PluginUtils.grantAdvancement(player, "lochness:root");
            if (!isInstalled) {
                player.sendMessage(Lochness.getPrefix() + ChatColor.YELLOW + "----------  WARNING  ----------");
                player.sendMessage(ChatColor.YELLOW + "Datapack seems to be corrupted, \"lochness:root\" couldn't be granted");
                player.sendMessage(ChatColor.YELLOW + "Try to install it manually and ensure, it is a valid uncorrupted zip file");
            }
        }
    }

}

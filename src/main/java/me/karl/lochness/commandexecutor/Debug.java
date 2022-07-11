package me.karl.lochness.commandexecutor;

import me.karl.lochness.Lochness;
import me.karl.lochness.entities.LochnessEntity;
import me.karl.lochness.entities.ceberos.Cerberus;
import me.karl.lochness.entities.lochness.LochnessBoss;
import me.karl.lochness.entities.watermonsters.hai.LochnessHai;
import me.karl.lochness.entities.watermonsters.hammerhai.LochnessHammerhai;
import me.karl.lochness.entities.watermonsters.krabbe.LochnessKrabbe;
import me.karl.lochness.entities.watermonsters.kraken.LochnessKraken;
import me.karl.lochness.entities.watermonsters.narwal.LochnessNarwal;
import me.karl.lochness.entities.watermonsters.orca.LochnessOrca;
import me.karl.lochness.entities.watermonsters.piranha.LochnessPiranha;
import me.karl.lochness.entities.watermonsters.rochen.LochnessRochen;
import me.karl.lochness.entities.watermonsters.turtle.LochnessTurtle;
import me.karl.lochness.structures.cave.CaveLogic;
import me.karl.lochness.structures.islands.PortalOpenEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Debug implements TabExecutor {

    public static boolean showHitbox = false;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        boolean isPlayer = true;
        if(!(sender instanceof Player))
            isPlayer = false;
        Location pLoc = ((Player)sender).getLocation();

        if(args.length < 2) {
            System.out.println(Lochness.getPrefix() + ChatColor.RED + "you need to use arguments");
            return false;
        }

        String s = args[1];
        switch(args[0]){
            case "spawn": {
                if(!isPlayer) {
                    sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "you need to be a player to execute this command");
                }

                if(s.equals("kraken"))
                    new LochnessKraken(pLoc);
                else if(s.equals("krabbe"))
                    new LochnessKrabbe(pLoc);
                else if(s.equals("hai"))
                    new LochnessHai(pLoc);
                else if(s.equals("hammerhai"))
                    new LochnessHammerhai(pLoc);
                else if(s.equals("rochen"))
                    new LochnessRochen(pLoc);
                else if(s.equals("schildkröte"))
                    new LochnessTurtle(pLoc);
                else if(s.equals("piranha"))
                    new LochnessPiranha(pLoc);
                else if(s.equals("orca"))
                    new LochnessOrca(pLoc);
                else if(s.equals("narwal"))
                    new LochnessNarwal(pLoc);
                else if(s.equals("cerberus"))
                    new Cerberus(pLoc);
                else if(s.equals("lochnessMonster"))
                    new LochnessBoss(pLoc);
                else
                    sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "this entityType doesn't exist");
                break;
            }
            case "kill": {
                if(!isPlayer) {
                    sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "you need to be a player to execute this command");
                }

                if(s.equals("kraken"))
                    LochnessEntity.removeEntities(LochnessKraken.class);
                else if(s.equals("krabbe"))
                    LochnessEntity.removeEntities(LochnessKrabbe.class);
                else if(s.equals("hai"))
                    LochnessEntity.removeEntities(LochnessHai.class);
                else if(s.equals("hammerhai"))
                    LochnessEntity.removeEntities(LochnessHammerhai.class);
                else if(s.equals("rochen"))
                    LochnessEntity.removeEntities(LochnessRochen.class);
                else if(s.equals("schildkröte"))
                    LochnessEntity.removeEntities(LochnessTurtle.class);
                else if(s.equals("piranha"))
                    LochnessEntity.removeEntities(LochnessPiranha.class);
                else if(s.equals("orca"))
                    LochnessEntity.removeEntities(LochnessOrca.class);
                else if(s.equals("narwal"))
                    LochnessEntity.removeEntities(LochnessNarwal.class);
                else if(s.equals("cerberus"))
                    LochnessEntity.removeEntities(Cerberus.class);
                else if(s.equals("lochnessMonster"))
                    LochnessEntity.removeEntities(LochnessBoss.class);
                else
                    sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "this entityType doesn't exist");
                break;
            }
            case "hitbox": {
                if(s.equals("show"))
                    showHitbox = true;
                else if(s.equals("hide"))
                    showHitbox = false;
                else
                    sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "unknown argument");
                break;
            }
            case "get": {
                if(s.equals("island")) {
                    TextComponent comp = new TextComponent(ChatColor.GREEN + "[" + Lochness.getIslandLoc().getBlockX() + ", ~, " + Lochness.getIslandLoc().getBlockZ() + "" + ChatColor.GREEN + "]");
                    comp.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/execute in minecraft:overworld run tp @s " + Lochness.getIslandLoc().getBlockX() + " ~ " + Lochness.getIslandLoc().getBlockZ()));
                    comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to teleport")));
                    sender.spigot().sendMessage(new TextComponent(Lochness.getPrefix() + "Islands at "), comp, new TextComponent("  in minecraft:overworld"));
                }
                else if(s.equals("hadesRoom")) {
                    TextComponent comp = new TextComponent(ChatColor.GREEN + "[" + Lochness.getRoomLoc().getBlockX() + ", ~, " + Lochness.getRoomLoc().getBlockZ() + "" + ChatColor.GREEN + "]");
                    comp.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/execute in minecraft:the_nether run tp @s " + Lochness.getRoomLoc().getBlockX() + " ~ " + Lochness.getRoomLoc().getBlockZ()));
                    comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to teleport")));
                    sender.spigot().sendMessage(new TextComponent(Lochness.getPrefix() + "Room at "), comp, new TextComponent("  in minecraft:the_nether"));
                }
                else
                    sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "unknown argument");
                break;
            }
            case "debug": {
                if(s.equals("monsterReadError")) {
                    LochnessEntity.resetAllEntities();
                    sender.sendMessage(Lochness.getPrefix() + "All Plugin-Entities have been removed and reset. Errors should be fixed now");
                }
                else if(s.equals("openPortal")) {
                    PortalOpenEvent.openPortal();
                }
                else if(s.equals("clearEntities")) {
                    CaveLogic.resetEntities();
                }
                else if(s.equals("respawnEntities")) {
                    CaveLogic.spawnEntities();
                }
                else if(s.equals("resetStructures")) {
                    CaveLogic.resetCave();
                }
                else if(s.equals("sendCommandFeedbackFalse")) {
                    for(World w: Bukkit.getWorlds()) {
                        w.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
                    }
                }
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if(args.length == 1) {
            list.add("spawn");
            list.add("kill");
            list.add("get");
            list.add("hitbox");
            list.add("debug");
        }
        if(args.length == 2) {
            switch(args[0]) {
                case "spawn":
                case "kill":
                    list.add("lochnessMonster");
                    list.add("cerberus");
                    list.add("kraken");
                    list.add("krabbe");
                    list.add("hai");
                    list.add("hammerhai");
                    list.add("schildkröte");
                    list.add("rochen");
                    list.add("piranha");
                    list.add("orca");
                    list.add("narwal");
                    break;
                case "get":
                    list.add("island");
                    list.add("hadesRoom");
                    break;
                case "hitbox":
                    list.add("show");
                    list.add("hide");
                    break;
                case "debug":
                    list.add("monsterReadError");
                    list.add("openPortal");
                    list.add("clearEntities");
                    list.add("respawnEntities");
                    list.add("resetStructures");
                    list.add("sendCommandFeedbackFalse");
                default:
                    break;
            }
        }
        return list;
    }
}

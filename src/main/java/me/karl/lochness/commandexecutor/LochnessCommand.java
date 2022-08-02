package me.karl.lochness.commandexecutor;

import me.karl.lochness.Lochness;
import me.karl.lochness.PluginUtils;
import me.karl.lochness.enchantments.poseidonspower.CustomEnchants;
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
import me.karl.lochness.structures.StructureLoader;
import me.karl.lochness.structures.cave.CaveLogic;
import me.karl.lochness.structures.cave.InteractionEvent;
import me.karl.lochness.structures.islands.PortalOpenEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LochnessCommand implements TabExecutor {

    public static boolean showHitbox = false;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        boolean isPlayer = true;
        if(!(sender instanceof Player))
            isPlayer = false;

        assert sender instanceof Player;
        Location pLoc = ((Player)sender).getLocation();
        Player p = (Player) sender;

        if(args.length < 2) {
            sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "you need to use arguments");
            return false;
        }

        switch (args[0]) {

            case "location":
                switch (args[1]) {
                    case "island":
                        TextComponent comp = new TextComponent(ChatColor.GREEN + "[" + Lochness.getIslandLoc().getBlockX() + ", ~, " + Lochness.getIslandLoc().getBlockZ() + "" + ChatColor.GREEN + "]");
                        comp.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/execute in minecraft:overworld run tp @s " + Lochness.getIslandLoc().getBlockX() + " ~ " + Lochness.getIslandLoc().getBlockZ()));
                        comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to teleport")));
                        sender.spigot().sendMessage(new TextComponent(Lochness.getPrefix() + "Islands at "), comp, new TextComponent("  in minecraft:overworld"));
                        break;

                    case "hadesRoom":
                        comp = new TextComponent(ChatColor.GREEN + "[" + Lochness.getRoomLoc().getBlockX() + ", ~, " + Lochness.getRoomLoc().getBlockZ() + "" + ChatColor.GREEN + "]");
                        comp.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/execute in minecraft:the_nether run tp @s " + Lochness.getRoomLoc().getBlockX() + " ~ " + Lochness.getRoomLoc().getBlockZ()));
                        comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to teleport")));
                        sender.spigot().sendMessage(new TextComponent(Lochness.getPrefix() + "Room at "), comp, new TextComponent("  in minecraft:the_nether"));
                        break;
                    default: sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "invalid arguments");
                }
                break;

            case "item":
                switch (args[1]) {
                    case "hadesKey":
                        int keyIndex = Integer.parseInt(args[2]);
                        ItemStack key = new ItemStack(Material.IRON_NUGGET);
                        ItemMeta meta = key.getItemMeta();
                        meta.setCustomModelData(keyIndex);
                        meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Hades Iron Key #" + keyIndex);
                        ArrayList<String> lore = new ArrayList<>();
                        lore.add(ChatColor.GRAY + "Go in the treasure-room");
                        lore.add(ChatColor.GRAY + "and put this key into");
                        lore.add(ChatColor.GRAY + "one of the itemframes");
                        meta.setLore(lore);
                        key.setItemMeta(meta);
                        p.getInventory().addItem(key);
                        break;

                    case "strengthKey":
                    case "defenseKey":
                    case "regenKey":
                        int customModelData = 0;
                        String name = "";
                        Boolean invalid = false;
                        switch (args[1]) {
                            case "strengthKey":
                                customModelData = 8;
                                name = ChatColor.DARK_RED + "Strength #";
                                break;
                            case "defenseKey":
                                customModelData = 6;
                                name = ChatColor.YELLOW + "Defense #";
                                break;
                            case "regenKey":
                                customModelData = 7;
                                name = ChatColor.BLUE + "Regeneration #";
                                break;
                            default: sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "invalid arguments"); invalid = true;
                        }
                        if (invalid)
                            break;
                        keyIndex = Integer.parseInt(args[2]);
                        key = new ItemStack(Material.IRON_NUGGET);
                        meta = key.getItemMeta();
                        meta.setCustomModelData(customModelData);
                        meta.setDisplayName(name + keyIndex);
                        lore = new ArrayList<>();
                        lore.add("Insert this key in the");
                        lore.add("associated itemframe to get access");
                        lore.add("to the next type of monster");
                        meta.setLore(lore);
                        key.setItemMeta(meta);
                        p.getInventory().addItem(key);
                        break;

                    case "lootkey":
                        key = new ItemStack(Material.GOLD_INGOT);
                        meta = key.getItemMeta();
                        meta.setCustomModelData(1);
                        meta.setDisplayName("loot key");
                        key.setItemMeta(meta);
                        p.getInventory().addItem(key);
                        break;

                    case "compassPart":
                        customModelData = 0;
                        invalid = false;
                        switch (args[2]) {
                            case "blue": customModelData = 1; break;
                            case "red": customModelData = 2; break;
                            case "orange": customModelData = 3; break;
                            case "purple": customModelData = 4; break;
                            default: sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "invalid arguments"); invalid = true;
                        }
                        if (invalid)
                            break;
                        key = new ItemStack(Material.JIGSAW);
                        meta = key.getItemMeta();
                        meta.setCustomModelData(customModelData);
                        meta.setDisplayName("Compasspart");
                        key.setItemMeta(meta);
                        p.getInventory().addItem(key);
                        break;

                    case "compass":
                        ItemStack compass = new ItemStack(Material.COMPASS);
                        CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();
                        compassMeta.setLodestone(Lochness.getIslandLoc());
                        compassMeta.setLodestoneTracked(false);
                        compassMeta.setDisplayName(ChatColor.BLUE + "Poseidons Compass");
                        compass.setItemMeta(compassMeta);
                        p.getInventory().addItem(compass);
                        break;

                    case "enchantedBook":
                        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
                        meta = book.getItemMeta();
                        lore = new ArrayList<>();
                        lore.add(ChatColor.BLUE + "Combat");
                        lore.add(ChatColor.GRAY + "Poseidons Power");
                        meta.setLore(lore);
                        meta.addEnchant(CustomEnchants.POSEIDONS_POWER, 1, false);
                        book.setItemMeta(meta);
                        p.getInventory().addItem(book);
                        break;

                    case "trident":
                        ItemStack trident = new ItemStack(Material.TRIDENT);
                        meta = trident.getItemMeta();
                        lore = new ArrayList<>();
                        lore.add(ChatColor.GRAY + "Poseidons Power");
                        meta.setLore(lore);
                        meta.addEnchant(CustomEnchants.POSEIDONS_POWER, 1, false);
                        trident.setItemMeta(meta);
                        p.getInventory().addItem(trident);
                        break;

                    case "guide":
                        Bukkit.dispatchCommand(sender,"give @p written_book{pages:['[\"\",{\"text\":\"  \",\"bold\":true,\"color\":\"dark_aqua\"},{\"text\":\"Lochness Guide\",\"bold\":true,\"underlined\":true,\"color\":\"dark_aqua\"},{\"text\":\"\\\\n\",\"color\":\"reset\"},{\"text\":\"Wichtig, TP installieren!\",\"color\":\"dark_red\"},{\"text\":\"\\\\nHi \",\"color\":\"reset\"},{\"selector\":\"@p\"},{\"text\":\", mit was willst du beginnen?\\\\n\"},{\"text\":\"Mit dem Weg zum Portal...\",\"color\":\"dark_gray\"},{\"text\":\"\\\\n\",\"color\":\"reset\"},{\"text\":\"...dann gehe zu S. 2\",\"italic\":true,\"color\":\"gray\"},{\"text\":\"\\\\n\",\"color\":\"reset\"},{\"text\":\"Mit dem, was du zum aktivieren des Portals brauchst...\",\"color\":\"dark_gray\"},{\"text\":\"\\\\n\",\"color\":\"reset\"},{\"text\":\"...dann gehe zu S. 3\",\"italic\":true,\"color\":\"gray\"},{\"text\":\"\\\\n\",\"color\":\"reset\"},{\"text\":\"Mit wichtigen Infos zum Endkampf...\",\"color\":\"dark_gray\"},{\"text\":\"\\\\n\",\"color\":\"reset\"},{\"text\":\"...dann gehe zu S. 12\",\"italic\":true,\"color\":\"gray\"}]','[\"\",{\"text\":\"Der Weg zum Portal\",\"underlined\":true,\"color\":\"dark_aqua\"},{\"text\":\"\\\\nUm das Portal zu finden musst \",\"color\":\"reset\"},{\"text\":\"     \\\\uEff4\\\\n\",\"color\":\"white\"},{\"text\":\"du \",\"color\":\"reset\"},{\"text\":\"alle\",\"bold\":true},{\"text\":\" diese Compassteile in Treasurechests\\\\nfinden und diese\\\\nzu einem Compass\\\\nzusammen\\\\ncraften, der dich\\\\nin die Richtung\\\\nzum Portal führt\",\"color\":\"reset\"}]','[\"\",{\"text\":\"Das Portal aktivieren\",\"underlined\":true,\"color\":\"dark_aqua\"},{\"text\":\"\\\\nUm das Portal zu aktivieren musst du einen Trident\",\"color\":\"reset\"},{\"text\":\"  \\\\uEff2\",\"color\":\"white\"},{\"text\":\"\\\\nmit einer \\\\nbesonderen \\\\nVerzauberung enchanten. Um dieses zu bekommen musst du folgendermaßen starten:\\\\n\\\\n\\\\n\",\"color\":\"reset\"},{\"text\":\"Nächste Seite -->\",\"italic\":true,\"color\":\"dark_gray\"}]','[\"\",{\"text\":\" \",\"color\":\"dark_aqua\"},{\"text\":\"1. Striderfood\",\"underlined\":true,\"color\":\"dark_aqua\"},{\"text\":\"\\\\nFinde Käfige an der Netherdecke\",\"color\":\"reset\"},{\"text\":\"\\\\uEff1\",\"color\":\"white\"},{\"text\":\"\\\\nund loote die \\\\nKiste, die in \\\\neinem der \\\\nKäfige spawnen \",\"color\":\"reset\"},{\"text\":\"kann\",\"bold\":true},{\"text\":\". Dort solltest du Striderfood finden, welches du im nächsten Schritt benötigst.\\\\n\\\\n\",\"color\":\"reset\"},{\"text\":\"Nächste Seite -->\",\"italic\":true,\"color\":\"dark_gray\"},{\"text\":\"\\\\n\\\\n \",\"color\":\"reset\"}]','[\"\",{\"text\":\"2. Finden des Raumes\",\"underlined\":true,\"color\":\"dark_aqua\"},{\"text\":\"\\\\nDanach musst Hades Raum finden. Dazu fütterst du einen Strider mit Striderfood, welcher dir dann die Richtung angibt. Der Raum befindet sich unter Höhe 30 und kann unter einem Lavasee, als auch unter Festland spawnen.\\\\n\",\"color\":\"reset\"},{\"text\":\"Nächste Seite -->\",\"italic\":true,\"color\":\"dark_gray\"}]','[\"\",{\"text\":\"3. Der Hades Raum\",\"underlined\":true,\"color\":\"dark_aqua\"},{\"text\":\"\\\\nVorsicht! Im Raum wartet jemand auf dich, Ceberus! Wenn du nicht leise bist (sneakst), dann greift er dich an!\",\"color\":\"reset\"},{\"text\":\"  \\\\uEff3\",\"color\":\"white\"},{\"text\":\"\\\\nDu hast \\\\neine Aufgabe: \\\\nFinde \",\"color\":\"reset\"},{\"text\":\"alle\",\"bold\":true},{\"text\":\" 5 versteckten Schlüssel. Suche dazu \",\"color\":\"reset\"},{\"text\":\"überall\",\"bold\":true},{\"text\":\".\",\"color\":\"reset\"},{\"text\":\"\\\\nNächste Seite -->\",\"italic\":true,\"color\":\"dark_gray\"},{\"text\":\"\\\\n \",\"color\":\"reset\"}]','[\"\",{\"text\":\"4. Die Schlüssel\",\"underlined\":true,\"color\":\"dark_aqua\"},{\"text\":\"\\\\nLege nun alle\",\"color\":\"reset\"},{\"text\":\"\\\\uEff5\",\"color\":\"white\"},{\"text\":\"\\\\nSchlüssel in die entsprechende Vorrichtung im Schatzraum. Daraus bekommst du rechts einen goldenen Schlüssel, mit dem du die Kiste auf der Gegenseite aufschließen kannst.\\\\n\\\\n\",\"color\":\"reset\"},{\"text\":\"Nächste Seite -->\",\"italic\":true,\"color\":\"dark_gray\"}]','[\"\",{\"text\":\" \\\\u0020 \",\"color\":\"dark_aqua\"},{\"text\":\"5. Das Buch\",\"underlined\":true,\"color\":\"dark_aqua\"},{\"text\":\"\\\\nAus der Kiste bekommst du ein Buch, welches du mit einem Trident kombinieren musst und später zum aktivieren des Portals brauchst.\\\\nTipp: Wirf den Trident doch mal auf einen Block im Nether.\\\\n\\\\n\\\\n\",\"color\":\"reset\"},{\"text\":\"Nächste Seite -->\",\"italic\":true,\"color\":\"dark_gray\"}]','[\"\",{\"text\":\" \\\\u0020 \",\"color\":\"dark_aqua\"},{\"text\":\"6. Das Portal\",\"underlined\":true,\"color\":\"dark_aqua\"},{\"text\":\"\\\\nWenn du das Portal schon gefunden hast...\\\\n\",\"color\":\"reset\"},{\"text\":\"...dann gehe zur nächsten Seite -->\",\"italic\":true,\"color\":\"dark_gray\"},{\"text\":\"\\\\nWenn du es noch nicht gefunden hast...\\\\n\",\"color\":\"reset\"},{\"text\":\"... <-- dann gehe zu Seite 1 und komm später wieder hierher zurück\",\"italic\":true,\"color\":\"dark_gray\"}]','[\"\",{\"text\":\"7. Die Aktivierung(1/2)\",\"underlined\":true,\"color\":\"dark_aqua\"},{\"text\":\"\\\\nWenn du auf der Insel stehst kannst du das Portal wie folgt aktivieren: Bringe zunächst in jeden der Türme einen Piglin-Brute. Dann solltest du einen Zoglin holen und diesen an den Zaun in der Mitte anbinden.\\\\n\\\\n\",\"color\":\"reset\"},{\"text\":\"Nächste Seite -->\",\"italic\":true,\"color\":\"dark_gray\"}]','[\"\",{\"text\":\"7. Die Aktivierung(2/2)\",\"underlined\":true,\"color\":\"dark_aqua\"},{\"text\":\"\\\\nWenn dieser auf dem Podest steht kannst du ihn mit dem enchanteten Trident abwerfen. Nun musst du nur noch eins machen: in das Portal gehen. Doch ließ dir zunächst die wichtigen Infos auf der kommenden Seite durch! \",\"color\":\"reset\"},{\"text\":\"-->\",\"italic\":true,\"color\":\"dark_gray\"}]','[\"\",{\"text\":\"Wichtige Infos\",\"bold\":true,\"underlined\":true,\"color\":\"dark_aqua\"},{\"text\":\"\\\\n1. keine Regeneration möglich, man kann nur Andere (mit\",\"color\":\"reset\"},{\"text\":\"  \\\\uEff6\",\"color\":\"white\"},{\"text\":\"\\\\nRechtsklick)\\\\ndurch einen\\\\nGolden Cod, den man aus einem Cod und acht Goldnuggets, wie eine golden Carrot craftet, heilen. Probier es einfach selber aus!\\\\n\",\"color\":\"reset\"},{\"text\":\"Nächste Seite -->\",\"italic\":true,\"color\":\"dark_gray\"}]','[\"\",{\"text\":\"2. Lochness wird \\\\ndurch Effekte unterstützt, diese \"},{\"text\":\"kannst\",\"bold\":true},{\"text\":\" du ihr entziehen, indem du Seitenmonster bekämpfst. Das sind Mobs, die eingesperrt in verschiedenen Bereichen der Höhle leben. Von jedem Effekt gibt es 3 Stufen.\\\\n\\\\n\",\"color\":\"reset\"},{\"text\":\"Nächste Seite -->\",\"italic\":true,\"color\":\"dark_gray\"}]','[\"\",{\"text\":\"Zunächst musst du das mob, welches die erste Stufe eines Effekts representiert besiegen. Durch das Abbauen des Conduits (welcher erst freigegeben wird sobald alle mobs von dieser Spezies tot sind) verschwindet der Effekt für Lochness.\\\\n\"},{\"text\":\"Nächste Seite -->\",\"italic\":true,\"color\":\"dark_gray\"}]','{\"text\":\"Aber man bekommt auch einen Schlüssel für den nächsten Raum, so kann man Lochness alle Effekte\\\\u0020entziehen. Wenn man bereit für den Endkampf ist lässt man Lochness mit der Glocke frei.\\\\n3. max 10x Sterben! Danach muss das Portal erneut aktiviert werden.\"}'],title:\"Lochness Guide\",author:\"Faincraft\",display:{Lore:[\"Dieses Buch wird dir helfen zurecht zu kommen.\"]}}");
                        break;

                    default: sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "invalid arguments");
                }
                break;

            case "entity":
                switch (args[1]) {
                    case "spawn":
                        switch (args[2]) {
                            case "kraken": new LochnessKraken(pLoc); break;
                            case "krabbe": new LochnessKrabbe(pLoc); break;
                            case "hai": new LochnessHai(pLoc); break;
                            case "hammerhai": new LochnessHammerhai(pLoc); break;
                            case "rochen": new LochnessRochen(pLoc); break;
                            case "schildkröte": new LochnessTurtle(pLoc); break;
                            case "piranha": new LochnessPiranha(pLoc); break;
                            case "orca": new LochnessOrca(pLoc); break;
                            case "narwal": new LochnessNarwal(pLoc); break;
                            case "cerberus": new Cerberus(pLoc); break;
                            case "lochnessMonster": new LochnessBoss(pLoc); break;
                            default: sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "invalid arguments");
                        }
                        break;

                    case "killType":
                        switch (args[2]) {
                            case "kraken": LochnessEntity.removeEntities(LochnessKraken.class); break;
                            case "krabbe": LochnessEntity.removeEntities(LochnessKrabbe.class); break;
                            case "hai": LochnessEntity.removeEntities(LochnessHai.class); break;
                            case "hammerhai": LochnessEntity.removeEntities(LochnessHammerhai.class); break;
                            case "rochen": LochnessEntity.removeEntities(LochnessRochen.class); break;
                            case "schildkröte": LochnessEntity.removeEntities(LochnessTurtle.class); break;
                            case "piranha": LochnessEntity.removeEntities(LochnessPiranha.class); break;
                            case "orca": LochnessEntity.removeEntities(LochnessOrca.class); break;
                            case "narwal": LochnessEntity.removeEntities(LochnessNarwal.class); break;
                            case "cerberus": LochnessEntity.removeEntities(Cerberus.class); break;
                            case "lochnessMonster": LochnessEntity.removeEntities(LochnessBoss.class); break;
                            default: sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "invalid arguments");
                        }
                        break;

                    case "hitbox":
                        switch (args[2]) {
                            case "show": showHitbox = true; break;
                            case "hide": showHitbox = false; break;
                            default: sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "invalid arguments");
                        }
                        break;

                    case "respawnAll":
                        CaveLogic.spawnEntities();
                        break;

                    case "killAll":
                        CaveLogic.clearEntities();
                        break;

                    case "resetDefault":
                        CaveLogic.clearEntities();
                        if (PortalOpenEvent.isPortalOpen)
                            CaveLogic.spawnEntities();
                        break;

                    default: sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "invalid arguments");
                }
                break;

            case "openPortal":
                switch (args[1]) {
                    case "island":
                        PortalOpenEvent.openPortal();
                        break;
                    case "lochnessCage":
                        InteractionEvent.openCage();
                        break;
                    default: sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "invalid arguments");
                }
                break;

            case "structure":
                switch (args[1]) {
                    case "generate":
                        switch (args[2]) {
                            case "island":
                                StructureLoader.generateIslands();
                                break;
                            case "cave":
                                StructureLoader.generateCave();
                                break;
                            case "hadesRoom":
                                StructureLoader.generateStructure(Lochness.getRoomLoc(), "lochness","lochness:hades_room");
                                break;
                            default: sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "invalid arguments");
                      }
                }
                break;

            case "datapack":
                switch (args[1]) {
                    case "downloadlink":
                        sender.sendMessage(Lochness.getPrefix() + "Datapack Downloadlink: " + ChatColor.YELLOW + Lochness.DATAPACK_DOWNLOAD_LINK);
                        break;
                    case "verify":
                        Boolean isInstalled = PluginUtils.grantAdvancement(p, "lochness:root");
                        if (isInstalled)
                            sender.sendMessage(Lochness.getPrefix() + "Datapack is installed correctly, \"lochness:root\" could be granted");
                        else {
                            sender.sendMessage(Lochness.getPrefix() + "Datapack is not correctly installed, \"lochness:root\" couldn't be granted");
                            sender.sendMessage("Try to install it manually and ensure, it is a valid uncorruptede zip file");
                        }
                        break;
                    default: sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "invalid arguments");
                }
                break;

            case "resourcepack":
                switch (args[1]) {
                    case "downloadlink":
                        sender.sendMessage(Lochness.getPrefix() + "Resourcepack Downloadlink: " + ChatColor.YELLOW + Lochness.RESOURCEPACK_DOWNLOAD_LINK);
                        break;
                }
                break;

            default: sender.sendMessage(Lochness.getPrefix() + ChatColor.RED + "invalid arguments");

        }

        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> list = new ArrayList<>();

        switch(args.length) {

            case 1:
                list.add("location");
                list.add("item");
                list.add("entity");
                list.add("openPortal");
                list.add("structure");
                list.add("datapack");
                list.add("resourcepack");
                break;

            case 2:
                switch(args[0]) {

                    case "location":
                        list.add("island");
                        list.add("hadesRoom");
                        break;

                    case "item":
                        list.add("hadesKey");
                        list.add("lootkey");
                        list.add("regenKey");
                        list.add("defenseKey");
                        list.add("strengthKey");
                        list.add("compassPart");
                        list.add("compass");
                        list.add("enchantedBook");
                        list.add("trident");
                        list.add("guide");
                        break;

                    case "entity":
                        list.add("spawn");
                        list.add("killType");
                        list.add("killAll");
                        list.add("respawnAll");
                        list.add("resetDefault");
                        list.add("hitbox");
                        break;

                    case "openPortal":
                        list.add("island");
                        list.add("lochnessCage");
                        break;

                    case "structure":
                        list.add("generate");
                        break;

                    case "datapack":
                        list.add("verify");
                        list.add("downloadlink");
                        break;

                    case "resourcepack":
                        list.add("downloadlink");
                        break;

                }
                break;

            case 3:
                switch(args[0]) {

                    case "item":
                        switch(args[1]) {
                            case "regenKey":
                            case "defenseKey":
                            case "strengthKey":
                                list.add("1");
                                list.add("2");
                                break;

                            case "hadesKey":
                                list.add("1");
                                list.add("2");
                                list.add("3");
                                list.add("4");
                                list.add("5");
                                break;

                            case "compassPart":
                                list.add("blue");
                                list.add("red");
                                list.add("purple");
                                list.add("orange");

                        }
                        break;

                    case "entity":
                        switch(args[1]) {
                            case "spawn":
                            case "killType":
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
                            case "hitbox":
                                list.add("show");
                                list.add("hide");
                        }

                    case "structure":
                        if (!args[1].equals("generate"))
                            break;
                        list.add("hadesRoom");
                        list.add("island");
                        list.add("cave");
                }
                break;

        }

        return list;
    }
}

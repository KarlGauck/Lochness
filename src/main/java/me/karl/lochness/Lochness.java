package me.karl.lochness;

import me.karl.lochness.Events.*;
import me.karl.lochness.commandexecutor.Debug;
import me.karl.lochness.commandexecutor.GuideCommand;
import me.karl.lochness.commandexecutor.MusicCommand;
import me.karl.lochness.enchantments.poseidonspower.CustomEnchants;
import me.karl.lochness.enchantments.poseidonspower.InventoryClickEvent;
import me.karl.lochness.entities.EntityLoadEvent;
import me.karl.lochness.entities.LochnessEntity;
import me.karl.lochness.entities.LochnessEntityHitEvent;
import me.karl.lochness.entities.ceberos.CeberosTriggerEvent;
import me.karl.lochness.entities.lochness.RegenEvent;
import me.karl.lochness.structures.BiomeFinder;
import me.karl.lochness.structures.StructureLoader;
import me.karl.lochness.structures.cage.CageLoadEvent;
import me.karl.lochness.structures.cave.*;
import me.karl.lochness.structures.endcredits.StopSpectating;
import me.karl.lochness.structures.hadesroom.ItemFrameEvent;
import me.karl.lochness.structures.hadesroom.RoomLoadEvent;
import me.karl.lochness.structures.islands.IslandLoadEvent;
import me.karl.lochness.structures.islands.PortalOpenEvent;
import me.karl.lochness.structures.islands.SandFallSchedule;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

public final class Lochness extends JavaPlugin {

    public static final long serialVersionUID = 0;
    public static int ticksBeforeStartup = 0;

    public static final String DATAPACK_DOWNLOAD_LINK = "https://www.dropbox.com/s/hhq86uzu8dajmml/lochness.zip?dl=1";
    public static final String RESOURCEPACK_DOWNLOAD_LINK = "https://www.dropbox.com/s/9no8t4wbli3bdm8/LochnessResources.zip?dl=1";

    public static boolean isDatapackInstalled = false;

    static Random ran = new Random();

    private static Location islandLoc;
    private static Location roomLoc;

    public static ShapedRecipe compassRecipe;
    public static ShapedRecipe goldenCodRecipe;

    public static long TIME_RUNNING = 0;
    public static boolean shutdown = false;
    public static boolean restart = true;
    private ShapedRecipe a;

    @Override
    public void onEnable() {
        // Plugin startup logic
        startupLogic();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        shutdown = true;
        LochnessEntity.saveEntities();
    }

    public void startupLogic() {
        if(Bukkit.getWorlds().size() < 3) {
            ticksBeforeStartup++;
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    startupLogic();
                }
            }, 1);
        } else {
            Bukkit.broadcastMessage(Lochness.getPrefix() + "Enabeling Lochness Functionality");

            ran.setSeed(Bukkit.getWorlds().get(1).getSeed());

            reloadDatapack();
            Bukkit.getPluginManager().registerEvents(new JoinEvent(), this);
            init();

            if(isDatapackInstalled)
                startFunctionality();
        }
    }

    private void startFunctionality() {
        registerCommands();
        registerEvents();
        registerScedules();
        registerEnchantments();
    }

    private void registerCommands() {
        getCommand("music").setExecutor(new MusicCommand());
        getCommand("music").setTabCompleter(new MusicCommand());
        getCommand("debugLochness").setExecutor(new Debug());
        getCommand("debugLochness").setTabCompleter(new Debug());
        getCommand("guide").setExecutor(new GuideCommand());
    }

    private void registerEvents() {

        //World generation
        Bukkit.getPluginManager().registerEvents(new CageLoadEvent(), this);
        Bukkit.getPluginManager().registerEvents(new RoomLoadEvent(), this);
        Bukkit.getPluginManager().registerEvents(new IslandLoadEvent(), this);

        //Entity events
        Bukkit.getPluginManager().registerEvents(new UseEvent(), this);
        Bukkit.getPluginManager().registerEvents(new RegenEvent(), this);
        Bukkit.getPluginManager().registerEvents(new InteractionEvent(), this);
        Bukkit.getPluginManager().registerEvents(new CeberosTriggerEvent(), this);
        Bukkit.getPluginManager().registerEvents(new LochnessEntityHitEvent(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathEvent(), this);
        Bukkit.getPluginManager().registerEvents(new BrutePortalEvent(), this);
        Bukkit.getPluginManager().registerEvents(new EntityLoadEvent(), this);

        //Structure events
        Bukkit.getPluginManager().registerEvents(new HangingBreak(), this);
        Bukkit.getPluginManager().registerEvents(new StopSpectating(), this);
        Bukkit.getPluginManager().registerEvents(new CageOpenEvent(), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakEvent(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceEvent(), this);
        Bukkit.getPluginManager().registerEvents(new EntitySpawnEvent(), this);
        Bukkit.getPluginManager().registerEvents(new ItemFrameEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PortalOpenEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MoveEvent(), this);
        Bukkit.getPluginManager().registerEvents(new IslandBlockEvent(), this);

        //General Events
        Bukkit.getPluginManager().registerEvents(new DeathEvent(), this);
        Bukkit.getPluginManager().registerEvents(new CompassCraftItem(), this);
        Bukkit.getPluginManager().registerEvents(new PiglinConvertEvent(), this);
        Bukkit.getPluginManager().registerEvents(new StriderFeedEvent(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new TridentWaterEvent(), this);
        Bukkit.getPluginManager().registerEvents(new SpawnEvent(), this);
    }

    private void registerScedules() {
        //Structure scedules
        Bukkit.getScheduler().runTaskTimer(this, SandFallSchedule.sandFallScedule(), 1, 1);
    }

    private void registerEnchantments() {
        CustomEnchants.register();
    }

    private static void registerRecipes() {
        Bukkit.broadcastMessage("Recipes start");

        // ---- Compass ----
        ItemStack compass = new ItemStack(Material.COMPASS);
        CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();
        compassMeta.setLodestone(Lochness.getIslandLoc());
        compassMeta.setLodestoneTracked(false);
        compassMeta.setDisplayName(ChatColor.BLUE + "Poseidons Compass");
        compass.setItemMeta(compassMeta);
        NamespacedKey compassKey = new NamespacedKey(Lochness.getPlugin(), "poseidons_compass");
        compassRecipe = new ShapedRecipe(compassKey, compass);
        compassRecipe.shape("AA", "AA");
        compassRecipe.setIngredient('A', Material.JIGSAW);
        Bukkit.addRecipe(compassRecipe);

        // ---- golden Cod ----
        ItemStack goldenCod = new ItemStack(Material.STRUCTURE_BLOCK);
        ItemMeta codMeta = goldenCod.getItemMeta();
        codMeta.setCustomModelData(2);
        codMeta.setDisplayName("Golden Cod");
        goldenCod.setItemMeta(codMeta);
        NamespacedKey codKey = new NamespacedKey(Lochness.getPlugin(), "golden_cod");
        goldenCodRecipe = new ShapedRecipe(codKey, goldenCod);
        goldenCodRecipe.shape("AAA", "ABA", "AAA");
        goldenCodRecipe.setIngredient('B', Material.COD);
        goldenCodRecipe.setIngredient('A', Material.GOLD_NUGGET);
        Bukkit.addRecipe(goldenCodRecipe);

        Bukkit.broadcastMessage("Recipes successfull");
    }

    private static void reloadDatapack() {

        String worldFolder = Bukkit.getWorlds().get(0).getName();

        File datapack = new File("./" + worldFolder + "/datapacks/lochness.zip");
        if (datapack.exists()) {
            isDatapackInstalled = true;
            return;
        }

        InputStream inputStream = null;

        try {
            inputStream = new URL(DATAPACK_DOWNLOAD_LINK).openStream();
            Files.copy(inputStream, Paths.get("./" + worldFolder + "/datapacks/lochness.zip"), StandardCopyOption.REPLACE_EXISTING);
            isDatapackInstalled = true;
        } catch (Exception e) {/* ----- NOTHING BAD CAN HAPPEN ----- */}

    }

    private void init() {
        Bukkit.getScheduler().runTaskLater(this, getEarlyInitCode(), 1).getTaskId();
        Bukkit.getScheduler().runTaskLater(this, getLateInitCode(), 4).getTaskId();
        Bukkit.getScheduler().runTaskTimer(this, getLoop(), 4, 1);
        Bukkit.getScheduler().runTaskTimer(this, StriderFeedEvent.getStriderLoop(), 4, 1);
    }

    public static Runnable getEarlyInitCode() {
        return new Runnable() {
            @Override
            public void run() {

                Bukkit.getWorlds().get(0).setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
                Bukkit.getWorlds().get(1).setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
                Bukkit.getWorlds().get(2).setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);

                if(!isDatapackInstalled)
                    Bukkit.broadcastMessage(Lochness.getPrefix() + "Download of Datapack failed... Manual installation required!");
                else {
                    islandLoc = BiomeFinder.getBiomeLocation(Biome.OCEAN, Biome.DEEP_OCEAN, Biome.DEEP_COLD_OCEAN, Biome.COLD_OCEAN,
                            Biome.LUKEWARM_OCEAN, Biome.DEEP_LUKEWARM_OCEAN, Biome.WARM_OCEAN);

                    roomLoc = new Location(Bukkit.getWorlds().get(1), ran.nextInt(14001)-7000, 1, ran.nextInt(14001) - 7000);

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "datapack list");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "datapack enable \"file/lochness.zip\"");

                    StructureLoader.pregenerateArea(roomLoc.clone(), 8);
                    StructureLoader.pregenerateArea(islandLoc.clone(), 14);
                    StructureLoader.pregenerateArea(new Location(Bukkit.getWorlds().get(2), 400, 0, 0), 10);
                }

            }
        };
    }

    public static Runnable getLateInitCode() {
        return new Runnable() {
            @Override
            public void run() {

                registerRecipes();

                LochnessEntity.loadEntities();

                StructureLoader.pregenerateArea(islandLoc.clone(), 14);
                CaveLogic.generateTotalCave();
                StructureLoader.fixLoadingIssues();

                restart = false;
            }
        };
    }

    public static Runnable getLoop() {
        return new Runnable() {
            @Override
            public void run() {
                TIME_RUNNING ++;

                // --- --- Poseidons Cave Particle --- ---
                getIslandLoc().getWorld().spawnParticle(Particle.CLOUD, getIslandLoc().add(new Vector(16.5, 40, 24)), 100, 1.2, 1, 0, 0.02);
                CaveLogic.particleLoop();
                InteractionEvent.particleLoop();
                SoundClass.soundLoop();
            }
        };
    }

    public static String getPrefix() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "Lochness" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "" + ChatColor.ITALIC;
    }

    public static Location getIslandLoc() {
        if(islandLoc == null) {
            islandLoc = BiomeFinder.getBiomeLocation(Biome.OCEAN, Biome.DEEP_OCEAN, Biome.DEEP_COLD_OCEAN, Biome.COLD_OCEAN,
                    Biome.LUKEWARM_OCEAN, Biome.DEEP_LUKEWARM_OCEAN, Biome.WARM_OCEAN);
        }
        return islandLoc.clone();
    }
    public static Location getRoomLoc() {
        if(roomLoc == null) {
            roomLoc = new Location(Bukkit.getWorlds().get(1), ran.nextInt(14001)-7000, 1, ran.nextInt(14001) - 7000);
        }
        return roomLoc.clone();
    }

    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("Lochness");
    }

}

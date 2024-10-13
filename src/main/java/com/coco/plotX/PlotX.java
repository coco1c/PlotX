package com.coco.plotX;

import com.coco.plotX.commands.PlotXCommand;
import com.coco.plotX.configuration.FileManager;
import com.coco.plotX.configuration.PlotDataSource;
import com.coco.plotX.hooks.HookManager;
import com.coco.plotX.hooks.extensions.WorldGuardHook;
import com.coco.plotX.plot.PlotManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import kotlin.jvm.JvmSynthetic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;

public final class PlotX extends JavaPlugin {
    public static PlotX instance;
    public static String ROOT_FOLDER;
    public static Map<String, Class> commands;
    public static Map<String, ProtectedRegion> plots;
    public ConsoleCommandSender console;
    public PluginManager pluginManager;
    public HookManager hookManager;
    public FileManager fileManager;
    public PlotManager plotManager;
    public PlotDataSource dataSource;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        ROOT_FOLDER = getDataFolder().toString();
        console = Bukkit.getConsoleSender();
        pluginManager = Bukkit.getPluginManager();

        commands = new HashMap<>();
        fileManager = new FileManager();

        hookManager = new HookManager();
        hookManager.hook();


        console.sendMessage(ChatColor.GOLD + "Enabled hooks " + hookManager.toString());

        fileManager.init();
        dataSource = new PlotDataSource();
        plotManager = new PlotManager(dataSource);
        registerPlots();
        commands.put("plotx", PlotXCommand.class);

        registerCommands();
    }

    @Override
    public void onDisable() {

    }
    public static boolean isLoaded(String pluginName){
        return PlotX.instance.pluginManager.getPlugin(pluginName) != null;
    }

    private void registerCommands() {
        for (Map.Entry<String, Class> entry : commands.entrySet()) {
            try {
                getCommand(entry.getKey()).setExecutor((org.bukkit.command.CommandExecutor) entry.getValue().newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    private void registerPlots() {
        plots = new HashMap<>();
        List<World> worlds = Bukkit.getWorlds();
        for (World world : worlds) {
            try {
                for (ProtectedRegion region : WorldGuardHook.Companion.getRegionContainer().get(BukkitAdapter.adapt(world)).getRegions().values()) {
                    if (plotManager.isPlot(region.getId())) {
                        plots.put(region.getId(), region);
                    }
                }
            }catch (Exception e){
            }
        }
    }

    @JvmSynthetic
    public static void runFor(int t, RunnableWithIndex r) {
        for (int i = 0; i < i; i++) {
            r.run(i);
        }
    }
    public static void runLater(int t, Runnable r) {
        Bukkit.getScheduler().runTaskLater(instance, r, t);
    }

    public static void runAsync(Runnable r) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, r);
    }

    public static void runSync(Runnable r) {
        Bukkit.getScheduler().runTask(instance, r);
    }

    @FunctionalInterface
    public interface RunnableWithIndex {
        void run(int index);
    }
}

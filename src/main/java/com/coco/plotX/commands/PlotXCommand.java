package com.coco.plotX.commands;

import com.coco.plotX.PlotX;
import com.coco.plotX.hooks.extensions.WorldGuardHook;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


public class PlotXCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) sender;
        if (args.length == 0){
            help(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("help")){
            help(sender);
            return true;
        }
        if(args.length == 1){
            if (player.hasPermission("plotx.owner")){
                if (args[0].equalsIgnoreCase("info")){
                    info(sender);
                    return true;
                }
                if (args[0].equalsIgnoreCase("tp")){
                    tp(sender);
                    return true;
                }
            }
        }
        return false;
    }

    private void tp(CommandSender sender) {
        Player player = (Player) sender;
        if (player.hasPermission("plotx.plot.owner") || player.hasPermission("plotx.plot.member")) {
            for (var entry : PlotX.plots.entrySet()) {
                String regionID = entry.getKey();
                if (PlotX.instance.plotManager.getDatabase().getPlotOwner(regionID).equals(player.getUniqueId().toString()) ||
                        PlotX.instance.plotManager.getDatabase().getPlotMembers(regionID).contains(player.getUniqueId().toString())) {
                    ProtectedRegion region = entry.getValue();
                    player.teleport(WorldGuardHook.Companion.adapt(region.getMinimumPoint()).toWorld(WorldGuardHook.Companion.getWorldForRegion(region))); //todo
                    break;
                }
            }
        }
    }

    private void help(CommandSender sender){
        Player player = (Player) sender;
        if (player.hasPermission("plotx.admin")) {
            player.sendMessage("§6PlotX Admin Help");
            player.sendMessage("§7/plotx help §8» §7Show this help message");
            player.sendMessage("§7/plotx reload §8» §7Reload the plugin");
            player.sendMessage("§7/plotx setup §8» §7Setup a new plot");
            player.sendMessage("§7/plotx delete §8» §7Delete a plot");
            player.sendMessage("§7/plotx edit §8» §7Edit a plot");
            player.sendMessage("§7/plotx list §8» §7List all plots");
            player.sendMessage("§7/plotx info §8» §7Get info about a plot");
            player.sendMessage("§7/plotx tp §8» §7Teleport to a plot");
        } else if (player.hasPermission("plotx.plot.owner")) {
            player.sendMessage("§6PlotX Owner Help");
            player.sendMessage("§7/plotx help §8» §7Show this help message");
            player.sendMessage("§7/plotx info §8» §7Get info about your plot");
            player.sendMessage("§7/plotx tp §8» §7Teleport to your plot");
        }else if (player.hasPermission("plotx.plot.member")) {
            player.sendMessage("§6PlotX Member Help");
            player.sendMessage("§7/plotx help §8» §7Show this help message");
            player.sendMessage("§7/plotx info §8» §7Get info about the plot you are in");
            player.sendMessage("§7/plotx tp §8» §7Teleport to the plot you are in");
        }else if (player.hasPermission("plotx.plot.none")){
            player.sendMessage("§6PlotX Help");
            player.sendMessage("§7Please buy a plot to use this command!");
        }
    }
    private void info(CommandSender sender){
        Player player = (Player) sender;
        if (player.hasPermission("plotx.owner")){
            player.sendMessage("§6Plot Info");
            player.sendMessage("§7Name: §8" + PlotX.instance.plotManager.getPlotRegionFromLocation(player.getLocation()).getId());
            player.sendMessage("§7Owner: §8" + PlotX.instance.plotManager.getDatabase().getPlotOwner(PlotX.instance.plotManager.getPlotRegionFromLocation(player.getLocation()).getId()));
            player.sendMessage("§7Members: §8" + PlotX.instance.plotManager.getDatabase().getPlotMembers(PlotX.instance.plotManager.getPlotRegionFromLocation(player.getLocation()).getId()));
        }else if (player.hasPermission("plotx.admin")){
            player.sendMessage("§6Plot Info");
            player.sendMessage("§7Name: §8" + PlotX.instance.plotManager.getPlotRegionFromLocation(player.getLocation()).getId());
            player.sendMessage("§7Owner: §8" + PlotX.instance.plotManager.getDatabase().getPlotOwner(PlotX.instance.plotManager.getPlotRegionFromLocation(player.getLocation()).getId()));
            player.sendMessage("§7Members: §8" + PlotX.instance.plotManager.getDatabase().getPlotMembers(PlotX.instance.plotManager.getPlotRegionFromLocation(player.getLocation()).getId()));
            player.sendMessage("§7Flags: §8" + PlotX.instance.plotManager.getPlotRegionFromLocation(player.getLocation()).getFlags());
            player.sendMessage("§7Region: §8" + PlotX.instance.plotManager.getPlotRegionFromLocation(player.getLocation()).getMinimumPoint() + " - " + PlotX.instance.plotManager.getPlotRegionFromLocation(player.getLocation()).getMaximumPoint());
            player.sendMessage("§7World: §8" + player.getWorld());
            player.sendMessage("§7Biome: §8" + player.getLocation().getBlock().getBiome());
        }
    }
}

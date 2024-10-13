package com.coco.plotX.listener;

import com.coco.plotX.PlotX;
import com.coco.plotX.hooks.extensions.WorldGuardHook;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Set;

public class PlayerMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location playerLocation = player.getLocation();

        RegionContainer container = WorldGuardHook.Companion.getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));

        if (regions != null) {
            ApplicableRegionSet regionSet = regions.getApplicableRegions(BukkitAdapter.asBlockVector(playerLocation));

            if (!regionSet.getRegions().isEmpty()) {
                for (ProtectedRegion region : regionSet.getRegions()) {
                    String regionID = region.getId();


                }
            }
        }
    }
    private boolean isPlotRegion(String regionName) {
        return PlotX.plots.containsKey(regionName);
    }
}

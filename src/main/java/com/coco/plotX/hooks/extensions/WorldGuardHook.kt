package com.coco.plotX.hooks.extensions

import com.coco.plotX.hooks.Extension
import com.coco.plotX.PlotX
import com.coco.plotX.exceptions.WorldGuardNotFoundException
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.managers.RegionManager
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import com.sk89q.worldguard.protection.regions.RegionContainer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.plugin.Plugin

class WorldGuardHook : Extension() {
    companion object{
        private var i: Int = 0
        private var i2: Int = 0
        private var i3: Int = 0
        fun getRegionContainer() : RegionContainer {
            return WorldGuard.getInstance().platform.regionContainer
        }
        fun getRegion(id: String): ProtectedRegion? {
            Bukkit.getWorlds().forEachIndexed { index, world ->
                val region = getRegionContainer().get(BukkitAdapter.adapt(world))?.getRegion(id)
                if (region != null) {
                    return region
                }
            }
            return null
        }
        fun getRegion(location: Location): ProtectedRegion? {
            return getRegionContainer().get(BukkitAdapter.adapt(location.world))?.getApplicableRegions(BukkitAdapter.asBlockVector(location))?.firstOrNull()
        }
        fun adapt(location: BlockVector3): Companion {
            i = location.x()
            i2 = location.y()
            i3 = location.z()
            return this
        }

        fun toWorld(world: World): Location {
            return Location(world, i.toDouble(), i2.toDouble(), i3.toDouble())
        }
        fun getWorldForRegion(region: ProtectedRegion): World? {
            val container: RegionContainer = getRegionContainer()

            for (world in Bukkit.getWorlds()) {
                val regionManager: RegionManager? = container.get(BukkitAdapter.adapt(world))
                if (regionManager != null && regionManager.hasRegion(region.id)) {
                    return world
                }
            }
            return null
        }
    }

    @Throws(WorldGuardNotFoundException::class)
    fun init() {
        var worldGuardPlugin: Plugin? = PlotX.instance.server.pluginManager.getPlugin("WorldGuard")

        if (worldGuardPlugin == null) {
            PlotX.instance.logger.warning("WorldGuard not found, trying to enable it")
            PlotX.instance.server.pluginManager.getPlugin("WorldGuard")?.let {
                PlotX.instance.server.pluginManager.enablePlugin(it)
            }
            worldGuardPlugin = PlotX.instance.server.pluginManager.getPlugin("WorldGuard")
        }

        if (worldGuardPlugin == null) {
            PlotX.instance.logger.severe("Failed to enable WorldGuard.")
            enabled = false
            PlotX.instance.pluginManager.disablePlugin(PlotX.instance)

            throw WorldGuardNotFoundException("WorldGuard plugin could not be found or enabled.", "Attempting to initialize WorldGuardHook")
        } else {
            PlotX.instance.logger.info("WorldGuard is enabled.")
            PlotX.instance.console.sendMessage("WorldGuard will now work with PlotX.")
            enabled = true
        }
    }

}

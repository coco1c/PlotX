package com.coco.plotX.plot

import com.coco.plotX.PlotX
import com.coco.plotX.Util.TextUtil
import com.coco.plotX.configuration.PlotDataSource
import com.coco.plotX.hooks.extensions.WorldGuardHook
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.protection.managers.RegionManager
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.w3c.dom.Text
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

class PlotManager(val database: PlotDataSource) {

    fun addPlot(regionID: String, playerID: String, duration: Duration) {
        if (!database.plotExists(regionID)) {
            createPlot(regionID)
        }
        val expiryTime = LocalDateTime.now().plus(duration)
        database.updatePlotOwner(regionID, playerID, expiryTime)
    }
    fun isPlot(regionID: String): Boolean {
        return database.plotExists(regionID)
    }

    fun plotExpired(regionID: String) {
        database.clearPlotOwner(regionID)
    }

    fun createPlot(regionID: String) {
        if (!database.plotExists(regionID)) {
            database.insertPlot(regionID)
        }
    }

    fun removePlot(regionID: String) {
        database.deletePlot(regionID)
    }

    fun addPlotMember(regionID: String, playerID: String) {
        database.addPlotMember(regionID, playerID)
    }

    fun getPlotMembers(regionID: String): List<String> {
        return database.getPlotMembers(regionID)
    }

    fun removePlotMember(regionID: String, playerID: String) {
        database.removePlotMember(regionID, playerID)
    }

    fun transferOwnershipForPlot(regionID: String, newOwnerID: String) {
        val currentOwner = database.getPlotOwner(regionID)
        if (currentOwner != null) {
            database.addPlotMember(regionID, currentOwner)
            database.getPlotExpiry(regionID)?.let { database.updatePlotOwner(regionID, newOwnerID, it) }
        }
    }

    fun forceClosePlot(regionID: String) {
        plotExpired(regionID)
    }

    fun forceAddTime(regionID: String, additionalTime: Duration) {
        val currentExpiry = database.getPlotExpiry(regionID) ?: return
        val newExpiry = currentExpiry.plus(additionalTime)
        database.updatePlotExpiry(regionID, newExpiry)
    }
    fun checkPlotEmpty(regionID: String): Boolean {
        val owner = database.getPlotOwner(regionID)
        return owner == null
    }
    fun getPlotOwner(regionID: String): String? {
        return database.getPlotOwner(regionID)
    }
    fun getPlotRegionFromLocation(location: Location): ProtectedRegion? {
        val regionManager: RegionManager = WorldGuardHook.getRegionContainer()?.get(BukkitAdapter.adapt(location.world)) ?: return null
        return regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location)).firstOrNull()
    }

    fun getItemOrCurrency(): String {
        return PlotX.instance.config.getString("plot.currency-type")?.toUpperCase() ?: "MONEY"
    }

    fun getItemIfApplicable(): ItemStack? {
        val currencyType = getItemOrCurrency()
        if (currencyType == "ITEM") {
            val itemConfig = PlotX.instance.config.getString("plot.buy-price") ?: return null
            return parseItemConfig(itemConfig)
        }
        return null
    }

    private fun parseItemConfig(itemConfig: String): ItemStack? {
        // Example itemConfig: "10_DIAMOND_SWORD[enchantment: example_power, hide_enchants: true, lore: example_string2, name: example, NBT: example]"
        val parts = itemConfig.split("_")
        if (parts.size < 2) return null

        val quantity = parts[0].toIntOrNull() ?: return null
        val itemName = parts[1]
        val itemStack = ItemStack(org.bukkit.Material.getMaterial(itemName) ?: return null, quantity)

        if (parts.size > 2 && parts[2].isNotEmpty()) {
            val attributes = parts[2].removeSurrounding("[", "]").split(", ")
            for (attribute in attributes) {
                val (key, value) = attribute.split(": ")
                when (key) {
                    "enchantment" -> {
                        val enchantParts = value.split("_")
                        if (enchantParts.size == 2) {
                            val enchantment = Enchantment.getByName(enchantParts[0].toUpperCase())
                            val level = enchantParts[1].toIntOrNull()
                            if (enchantment != null && level != null) {
                                itemStack.addUnsafeEnchantment(enchantment, level)
                            }
                        }
                    }
                    "hide_enchants" -> {
                        val meta = itemStack.itemMeta
                        if (meta != null) {
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                        }
                        itemStack.itemMeta = meta
                    }
                    "lore" -> {
                        val meta = itemStack.itemMeta
                        if (meta != null) {
                            meta.lore = TextUtil.colorize(value.split("_"))
                        }
                        itemStack.itemMeta = meta
                    }
                    "name" -> {
                        val meta = itemStack.itemMeta
                        if (meta != null) {
                            TextUtil.colorize(value)
                            meta.setDisplayName(value)
                        }
                        itemStack.itemMeta = meta
                    }
                    "NBT" -> {

                    }
                }
            }
        }
        return itemStack
    }
}

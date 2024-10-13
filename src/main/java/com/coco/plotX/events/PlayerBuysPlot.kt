package com.coco.plotX.events

import com.coco.plotX.PlotX
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.regions.CuboidRegion
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerBuysPlot(player: Player, regionID: String): Event(), Cancellable {
    private var cancelled = false
    private val player: Player = player
    private val regionID: String = regionID
    companion object {
        private val handlers = HandlerList()
        fun getHandlerList(): HandlerList = handlers
    }

    override fun getHandlers(): HandlerList = handlers
    override fun isCancelled(): Boolean {
        return this.cancelled
    }

    override fun setCancelled(p0: Boolean) {
        this.cancelled = p0
    }
    fun getPlayer(): Player {
        return player
    }
    fun getRegionID(): String {
        return regionID
    }
}
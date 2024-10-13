package com.coco.plotX.events

import com.coco.plotX.plots.Plot
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerJoinsPlot(player: Player, plot: Plot): Event() {
    companion object {
        private val handlers = HandlerList()
        fun getHandlerList(): HandlerList = handlers
    }

    override fun getHandlers(): HandlerList = handlers
}
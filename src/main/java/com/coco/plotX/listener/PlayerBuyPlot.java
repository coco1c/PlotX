package com.coco.plotX.listener;

import com.coco.plotX.events.PlayerBuysPlot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerBuyPlot implements Listener {
    @EventHandler
    public void onPlayerBuyPlot(PlayerBuysPlot event) {
        event.getPlayer();
    }
}

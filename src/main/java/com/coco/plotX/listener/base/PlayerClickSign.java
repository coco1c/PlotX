package com.coco.plotX.listener.base;

import com.coco.plotX.PlotX;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PlayerClickSign implements Listener {

    private static final Pattern REGION_ID_PATTERN = Pattern.compile("^[A-Za-z0-9_,'\\-+/]+$");

    @EventHandler
    public void onPlayerClickSign(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof Sign) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            String firstLine = sign.getLine(0);

            if (firstLine.startsWith("[plot-") && firstLine.endsWith("]")) {
                String regionId = firstLine.substring(6, firstLine.length() - 1);

                Matcher matcher = REGION_ID_PATTERN.matcher(regionId);
                if (matcher.matches()) {
                    if (PlotX.instance.plotManager.checkPlotEmpty(regionId)) {
                        String itemOrCurrency = PlotX.instance.plotManager.getItemOrCurrency();
                        switch (itemOrCurrency) {
                            case "ITEM":
                                /*ItemStack item = PlotX.instance.plotManager.getItem();*/
                                break;
                            case "MONEY":
                                /*PlotX.instance.plotManager.buyPlotWithCurrency(event.getPlayer(), regionId);*/
                                break;
                        }
                    }
                }
            }
        }
    }
}

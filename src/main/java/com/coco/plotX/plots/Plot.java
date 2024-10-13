package com.coco.plotX.plots;

import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.entity.Player;

import java.util.List;

public class Plot {
    public Player owner;
    public int regionID;
    public List<Player> members;
    public Plot(int regionID) {
        this.regionID = regionID;
    }


    @Override
    public String toString() {
        return "Plot{" +
                "owner=" + owner +
                ", regionID=" + regionID +
                ", members=" + members +
                '}';
    }
}

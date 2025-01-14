package com.minegolem.bossesArena.managers.regionManager;

import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;

public class BoxSelection {
    private Location first;
    private Location second;

    @Setter
    private World firstWorld;
    @Setter
    private World secondWorld;

    public void setFirst(Block block) {
        this.first = block.getLocation();
    }

    public void setSecond(Block block) {
        this.second = block.getLocation();
    }

    public boolean isComplete() {
        return first != null && second != null;
    }

    public BoundingBox buildBox() {
        if (firstWorld == secondWorld) {
            return BoundingBox.of(first.getBlock(), second.getBlock());
        }

        return null;
    }

    public World getWorld() {
        if (firstWorld == secondWorld) {
            return firstWorld;
        }

        return null;
    }
}

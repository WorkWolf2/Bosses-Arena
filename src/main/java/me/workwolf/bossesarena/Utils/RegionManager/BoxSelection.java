package me.workwolf.bossesarena.Utils.RegionManager;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;

public class BoxSelection {

    private Location first;
    private Location second;

    private World firstWorld;
    private World secondWorld;

    public void setFirst(Block block) {
        this.first = block.getLocation();
    }

    public void setSecond(Block block) {
        this.second = block.getLocation();
    }

    public void setFirstWorld(World world) {
        this.firstWorld = world;
    }

    public void setSecondWorld(World world) {
        this.secondWorld = world;
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

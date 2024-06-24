package me.workwolf.bossesarena.Utils.RegionManager;

import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public interface CustomRegion {
    String getName();

    Location getMinPoint();

    Location getMaxPoint();

    boolean isInsideRegion(Location location);

    List<Player> getPlayerInsideRegion();

    Collection<ActiveMob> getMobInsideRegion();
}

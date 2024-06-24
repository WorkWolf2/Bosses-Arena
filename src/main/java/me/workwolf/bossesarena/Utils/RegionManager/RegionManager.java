package me.workwolf.bossesarena.Utils.RegionManager;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RegionManager implements CustomRegion{
    private final String name;
    private final Location minPoint;
    private final Location maxPoint;

    public RegionManager(String name, Location minPoint, Location maxPoint) {
        this.name = name;
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getMinPoint() {
        return minPoint;
    }

    @Override
    public Location getMaxPoint() {
        return maxPoint;
    }

    @Override
    public boolean isInsideRegion(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        double minX = Math.min(minPoint.getX(), maxPoint.getX());
        double minY = Math.min(minPoint.getY(), maxPoint.getY());
        double minZ = Math.min(minPoint.getZ(), maxPoint.getZ());

        double maxX = Math.max(minPoint.getX(), maxPoint.getX());
        double maxY = Math.max(minPoint.getY(), maxPoint.getY());
        double maxZ = Math.max(minPoint.getZ(), maxPoint.getZ());

        return x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ;
    }

    @Override
    public List<Player> getPlayerInsideRegion() {
        List<Player> playersInside = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isInsideRegion(player.getLocation())) {
                playersInside.add(player);
            }
        }
        return playersInside;
    }

    @Override
    public Collection<ActiveMob> getMobInsideRegion() {
        Collection<ActiveMob> mobsInside = new ArrayList<>();
        for (ActiveMob mobs : MythicBukkit.inst().getMobManager().getActiveMobs()) {
            if (isInsideRegion(BukkitAdapter.adapt(mobs.getLocation()))) {
                mobsInside.add(mobs);
            }
        }
        return mobsInside;
    }
}

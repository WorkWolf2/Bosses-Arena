package me.workwolf.bossesarena.Utils.ConfigManager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import me.workwolf.bossesarena.BossesArena;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigManager {
    private final BossesArena plugin;

    public ConfigManager(BossesArena plugin) {
        this.plugin = plugin;
    }

    public String translate(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
            matcher = pattern.matcher(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    MiniMessage mm = MiniMessage.miniMessage();

    public Component parse(String message) {
        return mm.deserialize(message);
    }

    public Component getPrefix() {
        return parse(plugin.getConfig().getString("prefix"));
    }

    public Component getNoArena() {
        return getPrefix().append(parse(plugin.getConfig().getString("no-arena")));
    }

    public Component getNoArenas() {
        return getPrefix().append(parse(plugin.getConfig().getString("no-arenas")));
    }

    public Component getNoName() {
        return getPrefix().append(parse(plugin.getConfig().getString("no-name")));
    }

    public Component getSetKeyLock() {
        return getPrefix().append(parse(plugin.getConfig().getString("setkeylock")));
    }

    public Component getInventoryFull() {
        return getPrefix().append(parse(plugin.getConfig().getString("inventory-full")));
    }


    public Component getNoPerms() {
        return getPrefix().append(parse(plugin.getConfig().getString("no-perms")));
    }

    public Component getOffPlayer() {
        return getPrefix().append(parse(plugin.getConfig().getString("offline-player")));
    }

    public String getgavePlayer() {
        return (plugin.getConfig().getString("gave-key"));
    }

    public Component getAlreadyKeyLock() {
        return getPrefix().append(parse(plugin.getConfig().getString("already-keylock")));
    }

    public Component getRegionSetter() {
        return getPrefix().append(parse(plugin.getConfig().getString("regionsetter")));
    }

    public Component getAlreadyRegionSetter() {
        return getPrefix().append(parse(plugin.getConfig().getString("already-regionsetter")));
    }

    public Component getAlreadyRegion() {
        return getPrefix().append(parse(plugin.getConfig().getString("already-region")));
    }

    public Component GetRemovedLock() {
        return getPrefix().append(parse(plugin.getConfig().getString("removed-lock")));
    }

    public String BossRoomDone() {
        return plugin.getConfig().getString("bossroom-done");
    }

    public Component AlreadyArena() {
        return getPrefix().append(parse(plugin.getConfig().getString("already-arena")));
    }

    public Component CreatedArena() {
        return getPrefix().append(parse(plugin.getConfig().getString("created-arena")));
    }

    public Component SetLocation() {
        return getPrefix().append(parse(plugin.getConfig().getString("setted-location")));
    }

    public Component FirstPosition() {
        return getPrefix().append(parse(plugin.getConfig().getString("first-position")));
    }

    public Component SecondPosition() {
        return getPrefix().append(parse(plugin.getConfig().getString("second-position")));
    }

    public Component SuccessFullyRegion() {
        return getPrefix().append(parse(plugin.getConfig().getString("successfully-region")));
    }

    public Component impossibleToCreateRegion() {
        return getPrefix().append(parse(plugin.getConfig().getString("impossible-to-create-region")));
    }

    public Component differentWorlds() {
        return getPrefix().append(parse(plugin.getConfig().getString("different-worlds")));
    }

    public Component disabledFunction() {
        return getPrefix().append(parse(plugin.getConfig().getString("disabled-function")));
    }

    public Component deleteArenaSuccess() {
        return getPrefix().append(parse(plugin.getConfig().getString("deleted-arena-success")));
    }

    public Component deleteArenaFail() {
        return getPrefix().append(parse(plugin.getConfig().getString("deleted-arena-fail")));
    }

    public Component leftClick() {
        return getPrefix().append(parse(plugin.getConfig().getString("left-click")));
    }

    public Component getReloadCompleted() {
        return getPrefix().append(parse(plugin.getConfig().getString("reload-completed")));
    }

    public Component getSuccesfullyMob() {
        return getPrefix().append(parse(plugin.getConfig().getString("successfully-mob")));
    }

    public Location getGlobalSpawn() {
        World world = Bukkit.getWorld(plugin.getConfig().getString("globalspawnpoint.world"));

        double x = plugin.getConfig().getDouble("globalspawnpoint.x");
        double y = plugin.getConfig().getDouble("globalspawnpoint.y");
        double z = plugin.getConfig().getDouble("globalspawnpoint.z");

        float yaw = Float.parseFloat(String.valueOf(plugin.getConfig().getDouble("globalspawnpoint.yaw")));
        float pitch = Float.parseFloat(String.valueOf(plugin.getConfig().getDouble("globalspawnpoint.pitch")));

        return new Location(world, x, y, z, yaw, pitch);
    }

    public int getEndTime() {
        return  plugin.getConfig().getInt("end-time");
    }

    public boolean getUseTitle() {
        return plugin.getConfig().getBoolean("use-title");
    }

    public void sendTitle(Player player) {
        if (getUseTitle()) {
            final Component mainTitle = parse(plugin.getConfig().getString("end-title.title"));
            final Component subTitle = parse(plugin.getConfig().getString("end-title.subtitle"));

            final Title title = Title.title(mainTitle, subTitle);

            player.showTitle(title);
        }
    }

    public boolean getUseKey() {
        return plugin.getConfig().getBoolean("use-key");
    }

    public Sound getenterSound() {
        String soundName = plugin.getConfig().getString("enter-sound");

        return Sound.valueOf(soundName);
    }

    public String getTrue() {
        return translate(plugin.getConfig().getString("true"));
    }

    public String getFalse() {
        return translate(plugin.getConfig().getString("false"));
    }

}

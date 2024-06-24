package me.workwolf.bossesarena.Commands.subcommands;

import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Commands.SubCommand;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Files.FileManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class giveCommand extends SubCommand {

    private final FileManager fileManager = new FileManager();
    private final BossesArena plugin;

    public giveCommand(BossesArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "give a key to a player!";
    }

    @Override
    public String getSyntax() {
        return "<color:#ac8761>/ba give <arena name> <player name> <quantity> (-s)";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        ConfigManager settings = new ConfigManager(plugin);

        if (!settings.getUseKey()) {
            sender.sendMessage(settings.disabledFunction());
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(settings.getNoName());
            return;
        }

        if (args.length < 4 || args.length > 5) {
            sender.sendMessage(settings.getPrefix().append(settings.parse(getSyntax())));
            return;
        }

        if (!(sender.hasPermission("ba.*") || sender.hasPermission("ba.give") || sender.isOp())) {
            sender.sendMessage(settings.getNoPerms());
            return;
        }

        String arenaName = args[1];

        Map<String, Object> data = fileManager.getData(arenaName, plugin);

        if (!fileManager.checkFiles(plugin)) {
            sender.sendMessage(settings.getNoArenas());
            return;
        }

        if (!fileManager.checkFileInFolder(arenaName + ".yml", plugin)) {
            sender.sendMessage(settings.getNoArena());
            return;
        }

        Player target = null;
        try {
            target = Bukkit.getPlayer(args[2]);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        Map<String, Object> key = (Map<String, Object>) data.get("key");

        ItemStack keyItemStack = new ItemStack(Material.valueOf((String) key.get("material")));
        ItemMeta keyMeta = keyItemStack.getItemMeta();

        List<String> Rawlore = (List<String>) key.get("lore");
        List<String> lore = new ArrayList<>();

        for (String i : Rawlore) {
            lore.add(settings.translate(i));
        }

        assert keyMeta != null;
        if (key.containsKey("custom-model-data")) {
            int custom_model_data = (int) key.get("custom-model-data");
            keyMeta.setCustomModelData(custom_model_data);
        }

        String displayname = (String) key.get("name");

        keyMeta.setLore(lore);
        keyMeta.setDisplayName(settings.translate(displayname));

        PersistentDataContainer pdc = keyMeta.getPersistentDataContainer();
        pdc.set(new NamespacedKey(plugin, (String) data.get("name")), PersistentDataType.STRING, arenaName);

        keyItemStack.setItemMeta(keyMeta);

        if (target == null) {
            sender.sendMessage(settings.getOffPlayer());
            return;
        }

        if (target.getInventory().firstEmpty() == -1) {
            target.sendMessage(settings.getInventoryFull());
            return;
        }

        int quantity = Integer.parseInt(args[3]);

        for (int i = 0; i < quantity ; i++) {
            target.getInventory().addItem(keyItemStack);
        }

        if (args.length == 5) {
            String s = args[4];

            if (s.equalsIgnoreCase("-s")) {
                return;
            }
        }

        Component message = MiniMessage.miniMessage().deserialize(settings.getgavePlayer(), Placeholder.unparsed("quantity", String.valueOf(quantity)));
        target.sendMessage(settings.getPrefix().append(message));
    }
}

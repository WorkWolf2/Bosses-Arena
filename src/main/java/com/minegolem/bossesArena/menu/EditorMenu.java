package com.minegolem.bossesArena.menu;

import com.minegolem.bossesArena.Arena;
import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.managers.fileManager.FileManager;
import com.minegolem.bossesArena.managers.menu.InventoryButton;
import com.minegolem.bossesArena.managers.menu.InventoryGUI;
import com.minegolem.bossesArena.utils.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

@RequiredArgsConstructor
public class EditorMenu extends InventoryGUI {
    private final BossesArena plugin;

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 5 * 9, "BossesArena Editor");
    }

    @Override
    public void decorate(Player player) {
        FileManager fm = plugin.getFileManager();
        Map<String, Arena> arenaMap = fm.getArena();

        int i = 0;

        for (Map.Entry<String, Arena> entry : arenaMap.entrySet()) {
            this.addButton(i, this.createArenaEditorButton(entry.getValue()));
            i++;
        }

        super.decorate(player);
    }

    private InventoryButton createArenaEditorButton(Arena arena) {
        return new InventoryButton()
                .creator(player -> {
                    ItemStack item = new ItemStack(Material.IRON_BARS);
                    ItemMeta meta = item.getItemMeta();

                    assert meta != null;
                    meta.setDisplayName(ChatUtils.serialize(String.format("<bold><gray>%s</gray>", arena.getName())));

                    item.setItemMeta(meta);

                    return item;
                })
                .consumer(event -> {
                    Player player = (Player) event.getWhoClicked();

                    plugin.getGuiManager().openGUI(new ArenaEditorMenu(arena), player);
                });
    }
}

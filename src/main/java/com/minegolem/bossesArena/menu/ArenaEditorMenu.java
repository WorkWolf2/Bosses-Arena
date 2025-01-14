package com.minegolem.bossesArena.menu;

import com.minegolem.bossesArena.Arena;
import com.minegolem.bossesArena.managers.menu.InventoryGUI;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@RequiredArgsConstructor
public class ArenaEditorMenu extends InventoryGUI {
    private final Arena arena;

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 4 * 9, arena.getName());
    }

    @Override
    public void decorate(Player player) {

    }
}

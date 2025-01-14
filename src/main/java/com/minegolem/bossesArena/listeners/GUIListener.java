package com.minegolem.bossesArena.listeners;

import com.minegolem.bossesArena.managers.menu.GUIManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

@RequiredArgsConstructor
public class GUIListener implements Listener {
    private final GUIManager guiManager;

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        this.guiManager.handleClick(e);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        this.guiManager.handleClose(e);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        this.guiManager.handleOpen(e);
    }
}

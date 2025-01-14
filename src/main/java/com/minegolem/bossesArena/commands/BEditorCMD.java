package com.minegolem.bossesArena.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.menu.EditorMenu;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@CommandAlias("ba|bossesarena|barena")
public class BEditorCMD extends BaseCommand {

    private final BossesArena plugin;

    @Default
    @CommandPermission("bossesarena.admin.editor")
    @Subcommand("editor")
    @Description("Open the menu editor")
    public void onEdit(Player player) {
        plugin.getGuiManager().openGUI(new EditorMenu(plugin), player);
    }
}

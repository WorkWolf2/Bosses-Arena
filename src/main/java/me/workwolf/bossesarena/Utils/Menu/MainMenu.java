package me.workwolf.bossesarena.Utils.Menu;

import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Files.FileManager;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.button.StartPosition;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.ArrayList;
import java.util.List;


public class MainMenu extends Menu {

    @Position(start = StartPosition.CENTER)
    private final Button openArenasMenuButton;


    public MainMenu(BossesArena plugin) {
        FileManager fileManager = new FileManager();
        ConfigManager settings = new ConfigManager(plugin);

        setTitle(settings.translate("&8BOSSES ARENA"));
        setSize(9*5);

        this.openArenasMenuButton = new ButtonMenu(new ArenasMenu(this, fileManager.getItems(plugin), plugin), ItemCreator.of(CompMaterial.IRON_BARS,
                        "&a&lArenas",
                        " ",
                        settings.translate("&7Click to open Arenas Menu"))
                .make());
    }

    @Override
    public ItemStack getItemAt(int slot) {

        if (((slot >= 0 && slot <= 8) && (slot <= 1 || slot >= 7)) || (slot % 9 == 0 && slot != 18) || ((slot > 36 && slot < 44) && (slot <= 37 || slot >= 43)) || (((slot + 1) % 9 == 0 && slot != 26))) {
            return ItemCreator.of(CompMaterial.BLACK_STAINED_GLASS_PANE, "", "").make();
        }

        if ((slot >= 1 && slot <= 7) || slot == 18 || slot == 26 || (slot >= 38 && slot <= 42)) {
            return ItemCreator.of(CompMaterial.RED_STAINED_GLASS_PANE, "", "").make();
        }

        return NO_ITEM;
    }

}

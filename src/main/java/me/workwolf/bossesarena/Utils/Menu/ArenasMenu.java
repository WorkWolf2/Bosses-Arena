package me.workwolf.bossesarena.Utils.Menu;

import me.workwolf.bossesarena.BossesArena;
import me.workwolf.bossesarena.Utils.ConfigManager.ConfigManager;
import me.workwolf.bossesarena.Utils.Files.FileManager;
import me.workwolf.bossesarena.Utils.RegionManager.CustomRegion;
import me.workwolf.bossesarena.Utils.RegionManager.CustomRegionManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.StartPosition;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.Math.pow;
import static java.lang.Math.round;

public class ArenasMenu extends MenuPagged<ArenasItems> {

    @Position(start = StartPosition.BOTTOM_CENTER)
    private final Button createArenaButton;

    public static Map<UUID, Boolean> CreationMap = new HashMap<>();

    private final BossesArena plugin;

    public Map<UUID, Boolean> getCreationMap() {
        return CreationMap;
    }

    public ArenasMenu(MainMenu mainMenu, List<ArenasItems> itemsList, BossesArena plugin) {
        super(mainMenu, itemsList);
        this.plugin = plugin;

        setTitle("&a&lARENAS");
        setSize(9*6);

        this.createArenaButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                player.sendMessage("Type the name of the arena in chat or type 'Exit' to exit");

                CreationMap.put(player.getUniqueId(), true);
                menu.restartMenu();
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.NAME_TAG, "&7&lCreate new Arena", "").make();
            }
        };
    }

    @Override
    protected ItemStack convertToItemStack(ArenasItems item) {
        return ItemCreator.of(CompMaterial.IRON_BARS, item.getName(), "", "&7Open Arena Settings").make();
    }

    @Override
    protected void onPageClick(Player player, ArenasItems item, ClickType click) {
        new ArenaMenu(item.getName(), plugin).displayTo(player);
    }

    public class ArenaMenu extends Menu {
        @Position(value = 13)
        private final Button nameButton;

        @Position(start = StartPosition.CENTER)
        private final Button enterButton;

        @Position(start = StartPosition.CENTER, value = -1)
        private final Button mobButton;

        @Position(start = StartPosition.CENTER, value = 1)
        private final Button setPlayerLocButton;

        @Position(start = StartPosition.CENTER, value = 2)
        private final Button setBossLocButton;

        @Position(start = StartPosition.CENTER, value = -2)
        private final Button setRegionButton;

        @Position(value = 30)
        private final Button setLockButton;

        private final ConfigManager settings;

        public static Map<UUID, String> BossMap = new HashMap<>();

        public static Map<UUID, String> SpawnLocMap = new HashMap<>();

        public static Map<UUID, String> BossLocMap = new HashMap<>();

        public Map<UUID, String> getBossMap() {
            return BossMap;
        }

        public Map<UUID, String> getSpawnLocMap() {
            return SpawnLocMap;
        }

        public Map<UUID, String> getBossLocMap() {
            return BossLocMap;
        }

        public ArenaMenu(String name, BossesArena plugin) {
            super(ArenasMenu.this);
            FileManager fileManager = new FileManager();
            this.settings = new ConfigManager(plugin);

            setSize(9*6);
            setTitle(name);

            this.nameButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                    // TODO
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.NAME_TAG,
                            "&a&lArena Name",
                            "",
                            "&7" + name)
                            .make();
                }
            };

            this.enterButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                    plugin.getServer().dispatchCommand(player, "ba enter " + name + " " + player.getName());
                    player.getOpenInventory().close();
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.LEVER,
                            "&b&lEnter in arena",
                            "",
                            "&7Become a hero!")
                            .make();}
            };

            this.mobButton = new Button() {

                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                    if (!getBossMap().isEmpty()) return;

                    player.sendMessage("Type the mythicmob ID in chat or type 'Exit' to exit");
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3.0F, (float)pow(2.0, ((double)1 - 12.0) / 12.0));
                    getBossMap().put(player.getUniqueId(), name);
                    player.getOpenInventory().close();
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.WITHER_SKELETON_SKULL, "&4&lBoss",
                            fileManager.getBossName(plugin, name).equals("unset") ? "&cSet The Boss" : fileManager.getBossName(plugin, name))
                            .make();
                }
            };

            this.setPlayerLocButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                    if (!getSpawnLocMap().isEmpty()) return;

                    player.sendMessage("Type 'location' in chat or type 'Exit' to exit");
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3.0F, (float)pow(2.0, ((double)1 - 12.0) / 12.0));
                    getSpawnLocMap().put(player.getUniqueId(), name);
                    player.getOpenInventory().close();
                }

                @Override
                public ItemStack getItem() {
                    Location location = fileManager.getPlayerLoc(plugin, name);

                    return ItemCreator.of(CompMaterial.RED_BANNER, "&c&lSet Spawn Location",
                            "", location.getWorld() == null ? "&cLocation not setted-up" : "&7" + location.getWorld().getName() + "; " + round(location.getX()) + "; " +  round(location.getY()) + "; " + round(location.getZ()))
                            .make();
                    }
            };

            this.setBossLocButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                    if (!getBossLocMap().isEmpty()) return;

                    player.sendMessage("Type 'location' in chat or type 'Exit' to exit");
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 3.0F, (float)pow(2.0, ((double)1 - 12.0) / 12.0));
                    getBossLocMap().put(player.getUniqueId(), name);
                    player.getOpenInventory().close();
                }

                @Override
                public ItemStack getItem() {
                    Location location = fileManager.getBossLoc(plugin, name);

                    return ItemCreator.of(CompMaterial.BLACK_BANNER, "&c&lSet Boss Spawn Location",
                                    "", location.getWorld() == null ? "&cLocation not setted-up" : "&7" + location.getWorld().getName() + "; " + round(location.getX()) + "; " +  round(location.getY()) + "; " + round(location.getZ()))
                            .make();
                }
            };

            this.setRegionButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                    plugin.getServer().dispatchCommand(player, "ba region " + name);
                    player.getOpenInventory().close();
                }

                @Override
                public ItemStack getItem() {
                    CustomRegionManager regionManager = new CustomRegionManager(new File(plugin.getDataFolder() + File.separator + "regions.yml"));

                    CustomRegion region = regionManager.getRegionByName(name);

                    if (region != null) {
                        Location minPoint = region.getMinPoint();
                        Location maxPoint = region.getMaxPoint();

                        return ItemCreator.of(CompMaterial.GLOWSTONE_DUST, "&c&lSet Arena region",
                                        "", name, String.format("&7Min point: %d; %d; %d", round(minPoint.getX()), round(minPoint.getY()), round(minPoint.getZ())),
                                        String.format("&7Max point: %d; %d; %d", round(maxPoint.getX()), round(maxPoint.getY()), round(maxPoint.getZ()))).make();
                    }

                    return ItemCreator.of(CompMaterial.GLOWSTONE_DUST, "&c&lSet Arena region",
                            "", "&cRegion not setted-up").make();
                }
            };

            this.setLockButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                    Bukkit.dispatchCommand(player, "ba setlock " + name);
                    player.getOpenInventory().close();
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.TRIPWIRE_HOOK, "&6&lSet Lock", "", settings.getUseKey() ? "&7Set Lock" : "&cDisabled Function").make();
                }
            };

        }

        @Override
        public ItemStack getItemAt(int slot) {

            if (slot == 0 || slot == 1 || slot == 9) {
                return ItemCreator.of(CompMaterial.BLACK_STAINED_GLASS_PANE, "", "").make();
            }

            if (slot == 43 || slot == 44 || slot == 35) {
                return ItemCreator.of(CompMaterial.RED_STAINED_GLASS_PANE, "", "").make();
            }

            return NO_ITEM;
        }
    }
}


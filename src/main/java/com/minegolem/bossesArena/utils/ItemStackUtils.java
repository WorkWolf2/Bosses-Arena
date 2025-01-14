package com.minegolem.bossesArena.utils;

import io.th0rgal.oraxen.api.OraxenItems;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ItemStackUtils {

    /**
     * Custom serialize an ItemStack into a map.
     *
     * @param item ItemStack to serialize
     * @return Map representing the item
     */
    public static Map<String, Object> serializeItemStack(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }

        String itemId = OraxenItems.getIdByItem(item);

        Map<String, Object> serializedItem = new HashMap<>();
        if  (itemId != null) {
            serializedItem.put("oraxen_id", itemId);
            return serializedItem;
        }
        serializedItem.put("material", item.getType().name());

        ItemMeta meta = item.getItemMeta();

        assert meta != null;

        if (meta.hasDisplayName()) {
            serializedItem.put("name", ChatUtils.deserialize(meta.getDisplayName()));
        }

        if (meta.hasLore()) {
            List<String> lore = new ArrayList<>();

            assert meta.getLore() != null;
            meta.getLore().forEach(s -> lore.add(ChatUtils.deserialize(s)));

            serializedItem.put("lore", lore);
        }

        if (meta.hasEnchants()) {
            Map<String, Integer> enchantments = new HashMap<>();
            meta.getEnchants().forEach((enchantment, level) -> enchantments.put(enchantment.getKey().getKey(), level));
            serializedItem.put("enchantments", enchantments);
        }

        if (meta.hasCustomModelData()) {
            serializedItem.put("model-data", meta.getCustomModelData());
        }

        return serializedItem;
    }

    /**
     * Custom deserialize an ItemStack from a map.
     *
     * @param data Map representing the serialized item
     * @return Deserialized ItemStack
     */
    @SuppressWarnings("unchecked")
    public static ItemStack deserializeItemStack(Map<String, Object> data) {
        if (data == null) {
            return null;
        }

        if (data.containsKey("oraxen_id")) {
            return OraxenItems.getItemById((String) data.get("oraxen_id")).build();
        }

        Material material = Material.getMaterial((String) data.get("material"));

        assert material != null;

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        if (data.containsKey("name")) {
            meta.setDisplayName((String) data.get("name"));
        }

        if (data.containsKey("lore")) {
            meta.setLore((List<String>) data.get("lore"));
        }

        if (data.containsKey("enchantments")) {
            Map<String, Integer> enchantments = (Map<String, Integer>) data.get("enchantments");

            enchantments.forEach((enchantKey, level) -> {
                Enchantment enchantment = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(enchantKey));

                if (enchantment != null) {
                    meta.addEnchant(enchantment, level, true);
                }
            });
        }

        item.setItemMeta(meta);

        return item;
    }

}

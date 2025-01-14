package com.minegolem.bossesArena.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class YAMLUtils {

    public static Map<String, Object> yamlKeyToMap(ConfigurationSection section) {
        return section.getValues(true);
    }
}

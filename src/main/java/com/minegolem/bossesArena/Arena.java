package com.minegolem.bossesArena;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class Arena {

    public String name;
    public File file;
    public YamlConfiguration config;
}

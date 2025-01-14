package com.minegolem.bossesArena.managers.configManagers;

import com.minegolem.bossesArena.BossesArena;
import net.pino.simpleconfig.BaseConfig;
import net.pino.simpleconfig.annotations.Config;
import net.pino.simpleconfig.annotations.ConfigFile;
import net.pino.simpleconfig.annotations.Header;
import net.pino.simpleconfig.annotations.inside.Comment;
import net.pino.simpleconfig.annotations.inside.Path;

@Config
@ConfigFile("settings.yml")
@Header({"########################",
        "#      BossesArena     #",
        "#  Made by WorkWolf_2  #",
        "########################",
        "# Wiki at https://docs.minegolem.com/"})
public class Settings extends BaseConfig {

    public Settings(BossesArena plugin) {
        registerConfig(plugin);
    }

    @Path("database.type")
    public String database_type = "MYSQL";

    @Path("database.host")
    public String database_host = "localhost";

    @Path("database.port")
    public int database_port = 3306;

    @Path("database.name")
    public String database_name = "";

    @Path("database.username")
    public String database_username = "";

    @Path("database.password")
    public String database_password = "";

    @Path("database.maximum_pool_size")
    @Comment({"These are advanced options", "touch them carefully"})
    public int database_maximum_pool_size = 10;

    @Path("database.minimum_idle")
    public int database_minimum_idle = 10;

    @Path("database.max_lifetime")
    public int database_max_lifetime = 1800000;

    @Path("database.keepalive_time")
    public int database_keepalive_time = 0;

    @Path("database.connection_timeout")
    public int database_connection_timeout = 5000;

    @Path("database.parameters")
    public String database_parameters = String.join("&",
            "?autoReconnect=true", "useSSL=false",
            "useUnicode=true", "characterEncoding=UTF-8");

}

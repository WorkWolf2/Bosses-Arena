/*
 * This file is part of BossesArena, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.minegolem.bossesArena.database;

import com.minegolem.bossesArena.BossesArena;
import com.minegolem.bossesArena.User;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class Database {

    protected final BossesArena plugin;

    protected Database(BossesArena plugin) {
        this.plugin = plugin;
    }

    public abstract void initialize() throws IllegalStateException;

    public abstract void ensureUser(@NotNull User user);

    public abstract Optional<User> getUserByUuid(@NotNull UUID uuid);

    public abstract CompletableFuture<Optional<User>> getUserByUuidAsync(@NotNull UUID uuid);

    protected final String[] getSchemaStatements(@NotNull String schemaFileName) throws IOException {
        InputStream resourceStream = Objects.requireNonNull(plugin.getResource(schemaFileName),
                "Schema file not found: " + schemaFileName);

        String schemaContent = formatStatementTables(new String(resourceStream.readAllBytes(), StandardCharsets.UTF_8));

        resourceStream.close();

        return Arrays.stream(schemaContent.split(";"))
                .map(String::trim)
                .filter(statement -> !statement.isEmpty())
                .toArray(String[]::new);
    }

    protected final String formatStatementTables(@NotNull String sql) {
        return sql.replaceAll("%users_table%", TableName.USERS.getDefaultName());
    }

    public abstract CompletableFuture<List<User>> getAllUsers();

    public abstract void updateKill(@NotNull User user, int kill);

    public abstract void updateDeath(@NotNull User user, int death);

    public abstract void wipeDatabase();

    public abstract void terminate();

    @Getter
    public enum Type {
        MYSQL("MySQL", "mysql"),
        MARIADB("MariaDB", "mariadb"),
        SQLITE("SQLite", "sqlite");

        private final String displayName;
        private final String protocol;

        Type(@NotNull String displayName, @NotNull String protocol) {
            this.displayName = displayName;
            this.protocol = protocol;
        }
    }

    @Getter
    public enum TableName {
        USERS("bossesarena_users");

        private final String defaultName;

        TableName(@NotNull String defaultName) {
            this.defaultName = defaultName;
        }

        @NotNull
        private Map.Entry<String, String> toEntry() {
            return Map.entry(name(), defaultName);
        }

        @SuppressWarnings("unchecked")
        @NotNull
        public static Map<String, String> getDefaults() {
            return Map.ofEntries(Arrays.stream(values())
                    .map(TableName::toEntry)
                    .toArray(Map.Entry[]::new));
        }
    }

}

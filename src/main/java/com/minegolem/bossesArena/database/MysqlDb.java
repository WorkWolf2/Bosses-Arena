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
import com.minegolem.bossesArena.managers.configManagers.Settings;
import com.minegolem.bossesArena.utils.Logger;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.Map;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class MysqlDb extends Database {

    private final Settings settings;

    private static final String DATA_POOL_NAME = "BossesArenaHikariPool";
    private final String driverClass;
    private final String protocol;
    private HikariDataSource dataSource;

    public MysqlDb(BossesArena plugin) {
        super(plugin);

        this.settings = plugin.getSettings();

        final Type type = Type.valueOf(settings.database_type.toUpperCase());
        this.protocol = type.getProtocol();
        this.driverClass = type == Type.MARIADB ? "or.mariadb.jdbc.Driver" : "com.mysql.cj.jdbc.Driver";
    }

    @NotNull
    private Connection getConnection() throws SQLException {
        if (dataSource == null) throw new IllegalStateException("MySqlDB has not been initialized yet.");

        return dataSource.getConnection();
    }

    @Override
    public void initialize() throws IllegalStateException {
        dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setJdbcUrl(String.format("jdbc:%s://%s:%s/%s%s",
                protocol,
                settings.database_host,
                settings.database_port,
                settings.database_name,
                settings.database_parameters
        ));

        dataSource.setUsername(settings.database_username);
        dataSource.setPassword(settings.database_password);

        dataSource.setMaximumPoolSize(settings.database_maximum_pool_size);
        dataSource.setMinimumIdle(settings.database_minimum_idle);
        dataSource.setMaxLifetime(settings.database_max_lifetime);
        dataSource.setKeepaliveTime(settings.database_keepalive_time);
        dataSource.setConnectionTimeout(settings.database_connection_timeout);
        dataSource.setPoolName(DATA_POOL_NAME);

        final Properties properties = new Properties();
        properties.putAll(
                Map.of("cachePrepStmts", "true",
                        "prepStmtCacheSize", "250",
                        "prepStmtCacheSqlLimit", "2048",
                        "useServerPrepStmts", "true",
                        "useLocalSessionState", "true",
                        "useLocalTransactionState", "true"
                ));
        properties.putAll(
        Map.of(
                        "rewriteBatchedStatements", "true",
                        "cacheResultSetMetadata", "true",
                        "cacheServerConfiguration", "true",
                        "elideSetAutoCommits", "true",
                        "maintainTimeStats", "false")
        );
        dataSource.setDataSourceProperties(properties);

        try (Connection connection = dataSource.getConnection()) {

            final String[] databaseSchema = this.getSchemaStatements(String.format("database/%s_schema.sql", protocol));
            try (Statement statement = connection.createStatement()) {
                for (String tableCreationStatement : databaseSchema) {
                    statement.execute(tableCreationStatement);
                }
            } catch (SQLException e) {
                throw new IllegalStateException("Failed to create database tables. Please ensure you are running MySQL v8.0+ " +
                        "and that your connecting user account has privileges to create tables.", e);
            }
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("Failed to establish a connection to the MySQL database. " +
                    "Please check the supplied database credentials in the config file", e);
        }
    }

    @Override
    public void ensureUser(@NotNull User user) {
        getUserByUuid(user.uuid()).ifPresentOrElse(
            existingUser -> {
                if (!existingUser.uuid().equals(user.uuid())) {
                    try (Connection connection = getConnection();
                    PreparedStatement statement = connection.prepareStatement(formatStatementTables("""
                            UPDATE `%users_table%`
                            SET `username`=?
                            WHERE `uuid`=="""))) {

                        statement.setString(1, user.username());
                        statement.setString(2, existingUser.uuid().toString());
                        statement.executeUpdate();

                        Logger.log(Logger.LogLevel.INFO, "Updated " + user.username() + "'s name in the databse (" + existingUser.username() + " -> " + user.username() + ")");
                    } catch (SQLException e) {
                        Logger.log(Logger.LogLevel.ERROR, "Failed to fetch user by UUID", e);
                    }
                }
            },
            () -> {
                try (Connection connection = getConnection()) {
                    try (PreparedStatement statement = connection.prepareStatement(formatStatementTables("INSERT INTO `%users_table%` (`uuid`,`username`,`kill`,`death`) VALUES (?,?,?,?);"))) {
                        statement.setString(1, user.uuid().toString());
                        statement.setString(2, user.username());
                        statement.setInt(3, 0); // Initial kill count
                        statement.setInt(4, 0); // Initial death count
                        statement.execute();
                        Logger.log(Logger.LogLevel.INFO, "Inserted new user: " + user.username() + " with UUID: " + user.uuid());
                    } catch (SQLIntegrityConstraintViolationException e) {
                        Logger.log(Logger.LogLevel.ERROR, "Duplicate entry detected. User with UUID already exists: " + user.uuid());
                    } catch (SQLException e) {
                        Logger.log(Logger.LogLevel.ERROR, "Failed to insert a user into the database", e);
                    }
                } catch (SQLException e) {
                    Logger.log(Logger.LogLevel.ERROR, "Failed to open a database connection", e);
                }
            }
        );
    }

    @Override
    public Optional<User> getUserByUuid(@NotNull UUID uuid) {
            try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(formatStatementTables(
                        "SELECT `uuid`, `username`, `kill`, `death` FROM `%users_table%` WHERE `uuid` = ?"
                ))) {
                    statement.setString(1, uuid.toString());

                    final ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                         return Optional.of(new User(UUID.fromString(
                                resultSet.getString("uuid")),
                                resultSet.getString("username"),
                                resultSet.getInt("kill"),
                                resultSet.getInt("death"))
                        );
                    }
            } catch (SQLException e) {
                Logger.log(Logger.LogLevel.ERROR, "Failed to fetch a user from uuid from the database", e);
            }

            return Optional.empty();
    }

    @Override
    public CompletableFuture<Optional<User>> getUserByUuidAsync(@NotNull UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {

            return this.getUserByUuid(uuid);
        });
    }

    @Override
    public CompletableFuture<List<User>> getAllUsers() {
        return null;
    }

    @Override
    public void updateKill(@NotNull User user, int kill) {
        CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(formatStatementTables(
                         "UPDATE `%users_table%` SET `kill`=? WHERE `uuid`=?;"
                 ))) {
                statement.setInt(1, kill);
                statement.setString(2, user.uuid().toString());

                statement.executeUpdate();
            } catch (SQLException e) {
                Logger.log(Logger.LogLevel.ERROR, "Failed to update kill from the database", e);
            }
            return kill;
        });
    }

    @Override
    public void updateDeath(@NotNull User user, int death) {
        CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(formatStatementTables(
                         "UPDATE `%users_table%` SET `death`=? WHERE `uuid`=?;"
                 ))) {
                statement.setInt(1, death);
                statement.setString(2, user.uuid().toString());

                statement.executeUpdate();
            } catch (SQLException e) {
                Logger.log(Logger.LogLevel.ERROR, "Failed to update death from the database", e);
            }
            return death;
        });
    }

    @Override
    public void wipeDatabase() {
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(formatStatementTables("DELETE FROM `%arenas_table%`;"));
            }
        } catch (SQLException e) {
            Logger.log(Logger.LogLevel.ERROR, "Failed to wipe the database", e);
        }
    }

    @Override
    public void terminate() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}

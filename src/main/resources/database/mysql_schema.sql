# Set the storage engine
SET DEFAULT_STORAGE_ENGINE = INNODB;

# Enable foreign key constraints
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE IF NOT EXISTS `%users_table%`
(
    `uuid`     char(36)    NOT NULL UNIQUE,
    `username` varchar(16) NOT NULL,
    `kill` smallint NOT NULL,
    `death` smallint NOT NULL,

    PRIMARY KEY (`uuid`)
) CHARACTER SET utf8
  COLLATE utf8_unicode_ci;
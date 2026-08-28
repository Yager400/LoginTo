/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.data.database.connectors;

import com.github.yager400.loginto.common.data.files.FilesManager;
import com.zaxxer.hikari.HikariConfig;

import java.io.File;
import java.nio.file.Paths;

public class SQLiteConnector {

    public static HikariConfig getConfig(
            File pluginDataFolder
    ) {
        HikariConfig config = new HikariConfig();

        String sqlitePath = Paths.get(pluginDataFolder.getAbsolutePath().replace("\\", "/"), FilesManager.getPluginDataFolderName(), "database.db").toFile().getAbsolutePath();

        config.setJdbcUrl(String.format("jdbc:sqlite:%s", sqlitePath));
        config.setPoolName("LoginTo_SQLite_Pool");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setConnectionTestQuery("SELECT 1");
        config.setAutoCommit(true);
        config.setMinimumIdle(2);
        config.setMaximumPoolSize(5);
        config.addDataSourceProperty("journal_mode", "WAL");
        config.addDataSourceProperty("foreign_keys", "on");

        return config;
    }

}

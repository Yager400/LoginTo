/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.data.database.connectors;

import com.zaxxer.hikari.HikariConfig;

public class MySQLConnector {

    public static HikariConfig getConfig(
            String host,
            int port,
            String username,
            String password,
            String database
    ) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s", host, port, database));
        config.setUsername(username);
        config.setPassword(password);
        config.setPoolName("LoginTo_MySQL_Pool");
        config.setDriverClassName("com.github.yager400.loginto.libs.mysql.jdbc.Driver");
        config.setConnectionTestQuery("SELECT 1");
        config.setAutoCommit(true);
        config.setMinimumIdle(2);
        config.setMaximumPoolSize(5);

        return config;
    }

}

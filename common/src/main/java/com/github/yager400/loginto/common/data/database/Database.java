/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.data.database;

import com.github.yager400.loginto.common.data.database.migrations.V3_8ToV4_0MySQL;
import com.github.yager400.loginto.common.data.database.migrations.V3_8ToV4_0SQLite;
import com.github.yager400.loginto.common.utils.SecurityUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Database {

    private HikariDataSource source;
    public final String databaseType;

    public Database(HikariConfig hikariConfig, Path pluginDataSourcePath, String databaseType) {
        this.source = new HikariDataSource(hikariConfig);
        this.databaseType = databaseType;
        initDatabase(pluginDataSourcePath);
    }

    public void close() {
        if (!source.isClosed()) {
            source.close();
        }
    }

    public void executeQuery(String query, Object... objects) {
        try (Connection conn = source.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            int i = 1;
            for (Object object : objects) {
                pstmt.setObject(i, object);
                i++;
            }
            pstmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CachedRowSet executeQuerySet(String query, Object... objects) {
        try (Connection conn = source.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            int i = 1;
            for (Object object : objects) {
                pstmt.setObject(i, object);
                i++;
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
                crs.populate(rs);
                return crs;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initDatabase(Path pluginDataSourcePath) {

        executeQuery("CREATE TABLE IF NOT EXISTS LoginTo_Players_Accounts(" +
                "uuid TEXT NOT NULL," +
                "password TEXT NOT NULL," +
                "secret TEXT," +
                "isPremium BOOLEAN," +
                "isBedrock BOOLEAN," +
                "isCracked BOOLEAN," +
                "joinIp TEXT," +
                "joinEpoch BIGINT," +
                "lastLocationCords TEXT);"
        );

        if (databaseType.equalsIgnoreCase("sqlite")) {
            if (V3_8ToV4_0SQLite.canMigrate(pluginDataSourcePath)) {
                V3_8ToV4_0SQLite.migrate(pluginDataSourcePath, source);
            }
        }
        else if (databaseType.equalsIgnoreCase("mysql")) {
            try {
                V3_8ToV4_0MySQL.migrate(source);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void insertPlayer(UUID uuid, String password, String secret, Boolean isPremium, Boolean isBedrock, Boolean isCracked, String joinIP) {
        CompletableFuture.runAsync(() -> executeQuery(
                "INSERT INTO LoginTo_Players_Accounts(uuid, password, secret, isPremium, isBedrock, isCracked, joinIp, joinEpoch) VALUES (?, ?, ?, ?, ?, ?, ?, ?);",
                uuid.toString(),
                SecurityUtils.Hashing.hashString(password),
                secret,
                isPremium,
                isBedrock,
                isCracked,
                SecurityUtils.Hashing.hashString(joinIP),
                System.currentTimeMillis()/1000
        ));
    }

    public void updatePassword(UUID uuid, String password) {
        CompletableFuture.runAsync(() -> executeQuery(
                "UPDATE LoginTo_Players_Accounts SET password = ? WHERE uuid = ?;",
                SecurityUtils.Hashing.hashString(password),
                uuid.toString()
        ));
    }

    public void updateSecret(UUID uuid, String secret) {
        CompletableFuture.runAsync(() -> executeQuery(
                "UPDATE LoginTo_Players_Accounts SET secret = ? WHERE uuid = ?;",
                secret,
                uuid.toString()
        ));
    }

    public void updatePremium(UUID uuid, Boolean isPremium) {
        CompletableFuture.runAsync(() -> executeQuery(
                "UPDATE LoginTo_Players_Accounts SET isPremium = ? WHERE uuid = ?;",
                isPremium,
                uuid.toString()
        ));
    }

    public void updateBedrock(UUID uuid, Boolean isBedrock) {
        CompletableFuture.runAsync(() -> executeQuery(
                "UPDATE LoginTo_Players_Accounts SET isBedrock = ? WHERE uuid = ?;",
                isBedrock,
                uuid.toString()
        ));
    }

    public void updateCracked(UUID uuid, Boolean isCracked) {
        CompletableFuture.runAsync(() -> executeQuery(
                "UPDATE LoginTo_Players_Accounts SET isCracked = ? WHERE uuid = ?;",
                isCracked,
                uuid.toString()
        ));
    }


    public void updateSession(UUID uuid, String joinIp, int sessionDurationSeconds) {
        CompletableFuture.runAsync(() -> executeQuery(
                "UPDATE LoginTo_Players_Accounts SET joinIp = ?, joinEpoch = ? WHERE uuid = ?;",
                SecurityUtils.Hashing.hashString(joinIp),
                System.currentTimeMillis()/1000+sessionDurationSeconds,
                uuid.toString()
        ));
    }

    public void updateLastLocation(UUID uuid, String world, double x, double y, double z) {
        CompletableFuture.runAsync(() -> executeQuery(
                "UPDATE LoginTo_Players_Accounts SET lastLocationCords = ? WHERE uuid = ?;",
                String.format("%s;%s;%s;%s", world, x, y, z),
                uuid.toString()
        ));
    }

    public boolean databaseContainsPlayer(UUID uuid) {
        try {
            return executeQuerySet("SELECT 1 FROM LoginTo_Players_Accounts WHERE uuid = ?;", uuid.toString()).next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean playerHaveRecord(UUID uuid, String columnName) {
        try {
            return executeQuerySet("SELECT " + columnName + " FROM LoginTo_Players_Accounts WHERE uuid = ?;", uuid.toString()).next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isPasswordCorrect(UUID uuid, String password) {
        try {
            CachedRowSet set = executeQuerySet("SELECT password FROM LoginTo_Players_Accounts WHERE uuid = ?;", uuid.toString());
            if (set == null || !set.next()) {
                return false;
            }
            return SecurityUtils.Hashing.checkData(password, set.getString("password"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getSecret(UUID uuid) {
        try {
            CachedRowSet set =  executeQuerySet("SELECT secret FROM LoginTo_Players_Accounts WHERE uuid = ?;", uuid.toString());
            if (set == null || !set.next()) {
                return "";
            }
            return set.getString("secret");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean isPremium(UUID uuid) {
        try {
            CachedRowSet set = executeQuerySet("SELECT isPremium FROM LoginTo_Players_Accounts WHERE uuid = ?;", uuid.toString());
            if (set == null || !set.next()) {
                return false;
            }
            return set.getBoolean("isPremium");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isBedrock(UUID uuid) {
        try {
            CachedRowSet set = executeQuerySet("SELECT isBedrock FROM LoginTo_Players_Accounts WHERE uuid = ?;", uuid.toString());
            if (set == null || !set.next()) {
                return false;
            }
            return set.getBoolean("isBedrock");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isCracked(UUID uuid) {
        try {
            CachedRowSet set = executeQuerySet("SELECT isCracked FROM LoginTo_Players_Accounts WHERE uuid = ?;", uuid.toString());
            if (set == null || !set.next()) {
                return false;
            }
            return set.getBoolean("isCracked") && !isPremium(uuid);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isSessionEndedOrInvalid(UUID uuid, String userIP) {
        try {
            CachedRowSet set = executeQuerySet("SELECT joinIp, joinEpoch FROM LoginTo_Players_Accounts WHERE uuid = ?;", uuid.toString());
            if (set == null || !set.next()) {
                return true;
            }

            String userIPDatabase = set.getString("joinIp");
            if (userIPDatabase == null || userIP == null) {
                return true;
            }

            try {
                if (!SecurityUtils.Hashing.checkData(userIP, userIPDatabase)) {
                    return true;
                }
                long joinEpoch = set.getLong("joinEpoch");
                return joinEpoch <= System.currentTimeMillis() / 1000;
            } catch (Exception e) {
                // If the salt is invalid, the session is invalid
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public String getLastLocationCords(UUID uuid) {
        try {
            CachedRowSet set = executeQuerySet("SELECT lastLocationCords FROM LoginTo_Players_Accounts WHERE uuid = ?;", uuid.toString());
            if (set == null || !set.next()) {
                return null;
            }
            return set.getString("lastLocationCords");
        } catch (Exception e) {
            return null;
        }
    }

    public Object getObject(UUID uuid, String paramToGet) {
        try {
            CachedRowSet rs = executeQuerySet("SELECT * FROM LoginTo_Players_Accounts WHERE uuid = ?;", uuid.toString());
            if (rs != null && rs.next()) {
                return rs.getObject(paramToGet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removePlayerRecord(UUID uuid) {
        CompletableFuture.runAsync(() -> {
            executeQuery("DELETE FROM LoginTo_Players_Accounts WHERE uuid = ?;", uuid.toString());
        });
    }
}

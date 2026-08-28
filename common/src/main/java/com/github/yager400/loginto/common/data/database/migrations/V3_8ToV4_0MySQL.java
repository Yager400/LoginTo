/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.data.database.migrations;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class V3_8ToV4_0MySQL {

    public static void migrate(HikariDataSource source) throws Exception {
        CachedRowSet rowSet = null;
        try (Connection conn = source.getConnection()) {

            conn.prepareStatement("RENAME TABLE LoginTo_Users TO LoginTo_Users_backup").execute();
            conn.prepareStatement("RENAME TABLE LoginTo_PremiumAccStatus TO LoginTo_PremiumAccStatus_backup").execute();

            CachedRowSet setProvider = RowSetProvider.newFactory().createCachedRowSet();
            setProvider.populate(conn.prepareStatement("SELECT * FROM LoginTo_Users_backup").executeQuery());
            rowSet = setProvider;
        } catch (SQLException e) {
            // If the code is 1146 (table does not exist), return
            if (e.getErrorCode() != 1146) {
                e.printStackTrace();
            } else {
                return;
            }
        }

        if (rowSet == null) {
            throw new Exception("Invalid rowset");
        }

        try (Connection conn = source.getConnection()) {
            while (rowSet.next()) {
                try (PreparedStatement pstmtPremium = conn.prepareStatement("SELECT 1 FROM LoginTo_PremiumAccStatus_backup WHERE name = ?;")) {
                    pstmtPremium.setString(1, rowSet.getString("name"));
                    ResultSet premiumSet = pstmtPremium.executeQuery();
                    boolean isPremium = premiumSet.next() && premiumSet.getBoolean("isPremium");

                    UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + rowSet.getString("name")).getBytes(StandardCharsets.UTF_8));
                    String password = rowSet.getString("password");
                    String secret = rowSet.getString("secret");

                    try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO LoginTo_Players_Accounts(uuid, password, secret, isPremium) VALUES (?, ?, ?, ?);")) {
                        pstmt.setString(1, offlineUUID.toString());
                        pstmt.setString(2, password);
                        pstmt.setString(3, secret);
                        pstmt.setBoolean(4, isPremium);
                        pstmt.execute();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

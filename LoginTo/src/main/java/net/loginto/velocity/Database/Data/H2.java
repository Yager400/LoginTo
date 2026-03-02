/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */

package net.loginto.velocity.Database.Data;

import static net.loginto.velocity.Utility.FileMGR.YamlRead;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import org.h2.tools.Server;

import com.velocitypowered.api.proxy.ProxyServer;

import net.loginto.velocity.LoginTo;

public class H2 {

    private Statement stmt;
    private Connection conn;
    private final ProxyServer server;
    private final LoginTo plugin;

    private Server tcpServer;

    public H2(ProxyServer server, LoginTo plugin) {
        this.server = server;
        this.plugin = plugin;
    }

    public void connect() {
        try {
            
            Class.forName("org.h2.Driver");

            // Embedded connection
            Connection conn = DriverManager.getConnection(
                "jdbc:h2:./plugins/loginto/LoginTo_Sharing",
                "sa",
                ""
            );

            String port = YamlRead("database.database.port");

            tcpServer = Server.createTcpServer("-tcpPort", port, "-tcpAllowOthers").start();

            conn = DriverManager.getConnection(
                "jdbc:h2:tcp://localhost:" + port + "/./plugins/loginto/LoginTo_Sharing",
                "sa",
                ""
            );

            stmt = conn.createStatement();

            stmt.execute("create table if not exists AuthPlayers(username varchar(16), ispremium boolean)");
            stmt.execute("create table if not exists PlayersInfo(username varchar(16), ispremium boolean)");
            stmt.execute("create table if not exists LoggedPlayers(username varchar(16))");

            stmt.execute("delete from AuthPlayers");
            stmt.execute("delete from LoggedPlayers");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (tcpServer != null) {
            tcpServer.stop();
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertTempAuthPlayer(String username, boolean ispremium) {
        try {
            // If a user join spam
            stmt.execute("delete from AuthPlayers where username = '" + username + "'");

            stmt.execute("insert into AuthPlayers(username, ispremium) values ('" + username + "', " + ispremium + ")");

            server.getScheduler().buildTask(plugin, () -> {
                try {
                    stmt.execute("delete from AuthPlayers where username = '" + username + "'");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }).delay(5, TimeUnit.SECONDS).schedule();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPlayersInfo(String username, boolean ispremium) {
        try {
            stmt.execute("insert into PlayersInfo(username, ispremium) values ('" + username + "', " + ispremium + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removePlayersInfo(String username) {
        try {
            stmt.execute("delete from PlayersInfo where username = '" + username + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String accStatus(String username) {
        try {
            ResultSet result = stmt.executeQuery("select * from PlayersInfo where username = '" + username + "'");
            while (result.next()) {
                return result.getBoolean("ispremium") ? "premium" : "cracked";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "notindb";
    }

    public void removePlayerSession(String username) {
        try {
            stmt.execute("delete from LoggedPlayers where username = '" + username + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Boolean isPlayerLogged(String username) {
        try {
            ResultSet result = stmt.executeQuery("select * from LoggedPlayers where username = '" + username + "'");
            if (result.next()) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

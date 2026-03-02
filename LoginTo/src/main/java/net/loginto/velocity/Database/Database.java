/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */

package net.loginto.velocity.Database;

import static net.loginto.velocity.Utility.FileMGR.YamlRead;

import org.slf4j.Logger;

import com.velocitypowered.api.proxy.ProxyServer;

import net.loginto.velocity.Database.Data.*;

import net.loginto.velocity.LoginTo;

public class Database {
    
    private final Logger logger;

    private final H2 h2;
    private final MySql mysql;

    private final String dbType;

    public Database(ProxyServer server, LoginTo plugin, Logger logger) {
        this.logger = logger;
        this.dbType = YamlRead("database.database-type");

        this.mysql = new MySql(server, plugin);
        this.h2 = new H2(server, plugin);
        
    }

    public void connect() {
        switch (dbType) {
            case "mysql":
                mysql.connect();
                break;
            case "h2":
                h2.connect();
                break;
            default:
                logger.error("Database type is invalid (" + dbType + "), using H2");
                h2.connect();
                break;
        }
    }

    public void close() {
       switch (dbType) {
            case "mysql":
                mysql.close();
                break;
            case "h2":
                h2.close();
                break;
            default:
                logger.error("Database type is invalid (" + dbType + "), using H2");
                h2.close();
                break;
        }
    }
    
    public void insertTempAuthPlayer(String username, boolean ispremium) {
        switch (dbType) {
            case "mysql":
                mysql.insertTempAuthPlayer(username, ispremium);
                break;
            case "h2":
                h2.insertTempAuthPlayer(username, ispremium);
                break;
            default:
                logger.error("Database type is invalid (" + dbType + "), using H2");
                h2.insertTempAuthPlayer(username, ispremium);
                break;
        }
    }

    public void insertPlayersInfo(String username, boolean ispremium) {
        switch (dbType) {
            case "mysql":
                mysql.insertPlayersInfo(username, ispremium);
                break;
            case "h2":
                h2.insertPlayersInfo(username, ispremium);
                break;
            default:
                logger.error("Database type is invalid (" + dbType + "), using H2");
                h2.insertPlayersInfo(username, ispremium);
                break;
        }
    }

    public void removePlayersInfo(String username) {
        switch (dbType) {
            case "mysql":
                mysql.removePlayersInfo(username);
                break;
            case "h2":
                h2.removePlayersInfo(username);
                break;
            default:
                logger.error("Database type is invalid (" + dbType + "), using H2");
                h2.removePlayersInfo(username);
                break;
        }
    }

    public String accStatus(String username) {
        String accStatus = "null";
        switch (dbType) {
            case "mysql":
                accStatus = mysql.accStatus(username);
                break;
            case "h2":
                accStatus = h2.accStatus(username);
                break;
            default:
                logger.error("Database type is invalid (" + dbType + "), using H2");
                accStatus = h2.accStatus(username);
                break;
        }
        return accStatus;
    }

    public void removePlayerSession(String username) {
        switch (dbType) {
            case "mysql":
                mysql.removePlayerSession(username);
                break;
            case "h2":
                h2.removePlayerSession(username);
                break;
            default:
                logger.error("Database type is invalid (" + dbType + "), using H2");
                h2.removePlayerSession(username);
                break;
        }
    }

    public Boolean isPlayerLogged(String username) {
        Boolean isLogged = false;
        switch (dbType) {
            case "mysql":
                isLogged = mysql.isPlayerLogged(username);
                break;
            case "h2":
                isLogged = h2.isPlayerLogged(username);
                break;
            default:
                logger.error("Database type is invalid (" + dbType + "), using H2");
                isLogged = h2.isPlayerLogged(username);
                break;
        }
        return isLogged;
    }
}

/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Events;

import static net.loginto.bungeecord.Utility.CheckIfValidUsername.checkValidUsername;
import static net.loginto.bungeecord.Utility.FileMGR.YamlRead;
import static net.loginto.bungeecord.Utility.PremiumUserName.isUserNamePremium;

import net.loginto.bungeecord.LoginTo;
import net.loginto.bungeecord.Utility.AntiSpam;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.loginto.bungeecord.Database.Database;
import net.loginto.bungeecord.Database.SQLite;

public class PreLogin implements Listener {

    private final Database database;
    private final SQLite sqlite;
    private final AntiSpam antispam;

    public PreLogin(Database database, SQLite sqlite, ProxyServer server, LoginTo plugin) {
        this.database = database;
        this.sqlite = sqlite;
        this.antispam = new AntiSpam(server, plugin);
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent preLoginEvent) {


        PendingConnection event = preLoginEvent.getConnection();

        String ip = event.getAddress().getAddress().getHostAddress();

        if (antispam.isIpOver(ip)) {
            event.disconnect(YamlRead("anti_join-spam.ban-message"));
            return;
        }

        if (!Boolean.parseBoolean(YamlRead("loginto-premium-auth"))) {
            event.setOnlineMode(false); 
            return;
        }

        String username = event.getName();
        boolean usernameResult = checkValidUsername(username);

        String invalidMessage = YamlRead("messages.invalid-username").replace("%username%", username);

        if (!usernameResult) {
            event.disconnect(invalidMessage);
            return;
        }

        boolean UserNamePremium = isUserNamePremium(username, sqlite);

        if (UserNamePremium) {

            String AccStatus = database.accStatus(username);

            

            if (AccStatus.equals("premium")) {
                event.setOnlineMode(true);
                database.insertTempAuthPlayer(username, true);
            } 
            else if (AccStatus.equals("cracked")) {
                event.setOnlineMode(false);
                database.insertTempAuthPlayer(username, false);
            } 
            else if (AccStatus.equals("notindb")) {
                event.setOnlineMode(true);
                database.insertTempAuthPlayer(username, true);
            }

        } else {
            event.setOnlineMode(false);
            database.insertTempAuthPlayer(username, false);
        }

        antispam.incrementConnection(ip);
    }
}

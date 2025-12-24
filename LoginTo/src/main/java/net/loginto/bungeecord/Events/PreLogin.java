/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Events;

import static net.loginto.bungeecord.Utility.CheckIfValidUsername.checkValidUsername;
import static net.loginto.bungeecord.Utility.FileMGR.YamlRead;
import static net.loginto.bungeecord.Utility.PremiumUserName.isUserNamePremium;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.loginto.bungeecord.Database.H2;
import net.loginto.bungeecord.Database.SQLite;

public class PreLogin implements Listener {

    private final H2 h2;
    private final SQLite sqlite;

    public PreLogin(H2 h2, SQLite sqlite) {
        this.h2 = h2;
        this.sqlite = sqlite;
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent preLoginEvent) {


        PendingConnection event = preLoginEvent.getConnection();

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

            String AccStatus = h2.accStatus(username);

            

            if (AccStatus.equals("premium")) {
                event.setOnlineMode(true);
                h2.insertTempAuthPlayer(username, true);
            } 
            else if (AccStatus.equals("cracked")) {
                event.setOnlineMode(false);
                h2.insertTempAuthPlayer(username, false);
            } 
            else if (AccStatus.equals("notindb")) {
                event.setOnlineMode(true);
                h2.insertTempAuthPlayer(username, true);
            }

        } else {
            event.setOnlineMode(false);
            h2.insertTempAuthPlayer(username, false);
        }
    }
}

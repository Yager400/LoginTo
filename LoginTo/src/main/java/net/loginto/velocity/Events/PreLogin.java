/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.velocity.Events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import static net.loginto.velocity.Utility.PremiumUserName.isUserNamePremium;

import static net.loginto.velocity.Utility.CheckIfValidUsername.checkValidUsername;
import static net.loginto.velocity.Utility.FileMGR.YamlRead;

import net.kyori.adventure.text.Component;
import net.loginto.velocity.Database.H2;
import net.loginto.velocity.Database.SQLite;


public class PreLogin {

    private final H2 h2;
    private final SQLite sqlite;

    public PreLogin(ProxyServer server, H2 h2, SQLite sqlite) {
        this.h2 = h2;
        this.sqlite = sqlite;
    }



    @Subscribe
    public void onPreLogin(PreLoginEvent event) {

        if (!Boolean.parseBoolean(YamlRead("loginto-premium-auth"))) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.forceOfflineMode()); 
            return;
        }

        boolean usernameResult = checkValidUsername(event);

        String invalidMessage = YamlRead("messages.invalid-username").replace("%username%", event.getUsername());

        if (!usernameResult) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(Component.text(invalidMessage)));
            return;
        }

        boolean UserNamePremium = isUserNamePremium(event.getUsername(), sqlite);
        

        if (UserNamePremium) {

            

            String AccStatus = h2.accStatus(event.getUsername());

            if (AccStatus.equals("premium")) {
                // This is a player that have executed the /premium command, in any case we will use the online authentication
                event.setResult(PreLoginEvent.PreLoginComponentResult.forceOnlineMode());
                h2.insertTempAuthPlayer(event.getUsername(), true);
            } 
            else if (AccStatus.equals("cracked")) {
                // This account is cracked and executed the /cracked command, but have joined before this premium name was registered by mojang
                event.setResult(PreLoginEvent.PreLoginComponentResult.forceOfflineMode());
                h2.insertTempAuthPlayer(event.getUsername(), false);
            } 
            else {
                if (AccStatus.equals("notindb")) {
                    // This player never joined in this server, for not let him stealing this name, we will require the premium authentication
                    event.setResult(PreLoginEvent.PreLoginComponentResult.forceOnlineMode());
                    h2.insertTempAuthPlayer(event.getUsername(), true);
                }
            }

            
        } else {
            // 100% cracked player
            event.setResult(PreLoginEvent.PreLoginComponentResult.forceOfflineMode());
            h2.insertTempAuthPlayer(event.getUsername(), false);
        }


    }
}

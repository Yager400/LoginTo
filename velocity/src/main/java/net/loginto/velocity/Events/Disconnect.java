/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.velocity.Events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import net.loginto.common.PlayerUtils.Sessions;

public class Disconnect {

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        Sessions.removePlayer(event.getPlayer().getUniqueId());
        Sessions.removeBorrowedData(event.getPlayer().getUniqueId());
    }
}

/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.velocity.Events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import net.loginto.common.PlayerUtils.Sessions;

public class PluginMessage {

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getSource() instanceof Player player) {
            if (!Sessions.isPlayerLogged(player.getUniqueId())) {
                event.setResult(PluginMessageEvent.ForwardResult.handled());
            }
        }
    }

}

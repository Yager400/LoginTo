/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Events;

import net.md_5.bungee.api.plugin.Listener;

import java.util.logging.Logger;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;


import net.loginto.bungeecord.Database.H2;

public class PluginMessage implements Listener {

    private final Logger logger;
    private final H2 h2;
    public static String channel = "loginto:authchannel";

    public PluginMessage(ProxyServer server, H2 h2, Logger logger) {
        this.logger = logger;
        this.h2 = h2;

        server.registerChannel(channel);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {

        if (!event.getTag().equals(channel)) return;
        if (!(event.getSender() instanceof net.md_5.bungee.api.connection.Server)) return;

        try {
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            String subChannel = in.readUTF();
            String username = in.readUTF();

            switch (subChannel) {
                case "AddPlayersInfo":
                    Boolean ispremium = Boolean.valueOf(in.readUTF());
                    h2.removePlayersInfo(username);
                    h2.insertPlayersInfo(username, ispremium);
                    break;
                case "RemovePlayersInfo":
                    h2.removePlayersInfo(username);
                    break;
                
                
                default:
                    logger.warning("Subchannel not found in the channel " + channel + " , please make sure that the request is ok");
                    break;
            }

        } catch (Exception e) {
            logger.severe("Error managing the request, make sure that this is the right channel: " + channel + "\nMore info about the error: " + e);
        }
    }
}
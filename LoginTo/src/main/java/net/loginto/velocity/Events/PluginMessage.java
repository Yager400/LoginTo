/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */

package net.loginto.velocity.Events;

import org.slf4j.Logger;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import net.loginto.velocity.Database.Database;

public class PluginMessage {
    
    
    private final Logger logger;

    private final Database database;


    public static String channel = "loginto:authchannel";
    private static final MinecraftChannelIdentifier CHANNEL_ID = MinecraftChannelIdentifier.from(channel);

    public PluginMessage(ProxyServer server, Database database, Logger logger) {
        this.logger = logger;
        this.database = database;

        server.getChannelRegistrar().register(CHANNEL_ID);
    }




    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {

        if (!event.getIdentifier().getId().equals(channel)) return;
        if (!(event.getSource() instanceof ServerConnection)) return;
        
        try {
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            String subChannel = in.readUTF();

            String username = in.readUTF();
            

            switch (subChannel) {
                case "AddPlayersInfo":
                    Boolean ispremium = Boolean.valueOf(in.readUTF());
                    database.removePlayersInfo(username);
                    database.insertPlayersInfo(username, ispremium);
                    break;
                case "RemovePlayersInfo":
                    database.removePlayersInfo(username);
                    break;
                default:
                    logger.warn("Subchannel not found in the channel " + channel + " , please make sure that the request is ok");
                    break;
            }


        } catch (Exception e) {
            logger.error("Error managing the request, make sure that this is the right channel: " + channel + "\nMore info about the error: " + e);
        }
    }


}

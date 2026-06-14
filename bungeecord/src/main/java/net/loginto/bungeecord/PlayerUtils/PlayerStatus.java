/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.PlayerUtils;

import net.loginto.bungeecord.Utils.Files.LoginToFiles;
import net.loginto.common.PlayerUtils.Sessions;
import net.loginto.common.PlayerUtils.Tries;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.common.Utils.Files.MessageKeys;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;

public class PlayerStatus {

    public static void setPlayerAsLogged(ProxiedPlayer player, ProxyServer server) {

        Sessions.addPlayer(player.getUniqueId());

        Tries.resetTries(player.getUniqueId());

        ServerInfo lobbyServer = Servers.getRegisteredLobbyServer(player, server);

        player.connect(lobbyServer);
    }

    public static void setPlayerAsNotLogged(ProxiedPlayer player, ProxyServer server) {

        Sessions.removePlayer(player.getUniqueId());

        ServerInfo authServer = Servers.getRegisteredAuthServer(player, server);

        player.connect(authServer);
    }

    public static void setPlayerAsLogged(ServerConnectEvent event, ProxyServer server) {
        Sessions.addPlayer(event.getPlayer().getUniqueId());

        Tries.resetTries(event.getPlayer().getUniqueId());

        event.setTarget(Servers.getRegisteredLobbyServer(event.getPlayer(), server));
    }

    public static void setPlayerAsNotLogged(ServerConnectEvent event, ProxyServer server) {
        Sessions.removePlayer(event.getPlayer().getUniqueId());

        event.setTarget(Servers.getRegisteredAuthServer(event.getPlayer(), server));
    }

    protected static class Servers {
        public static ServerInfo getRegisteredAuthServer(ProxiedPlayer player, ProxyServer server) {
            for (Object serverObject : LoginToFiles.Config.getList(ConfigKeys.SERVERS_AUTH_SERVERS.path())) {
                ServerInfo authServer = server.getServerInfo(serverObject.toString());

                return authServer;
            }
            player.disconnect(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.SERVERS_AUTH_SERVERS_ERROR.path()));
            return null;
        }

        public static ServerInfo getRegisteredLobbyServer(ProxiedPlayer player, ProxyServer server) {
            for (Object serverObject : LoginToFiles.Config.getList(ConfigKeys.SERVER_DESTINATION_SERVERS.path())) {
                ServerInfo destinationServer = server.getServerInfo(serverObject.toString());

                return destinationServer;
            }
            player.disconnect(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.SERVERS_DESTINATIONS_SERVERS_ERROR.path()));
            return null;
        }
    }
}

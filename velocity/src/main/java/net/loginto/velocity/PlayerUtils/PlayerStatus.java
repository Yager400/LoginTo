/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.velocity.PlayerUtils;

import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.loginto.common.PlayerUtils.Sessions;
import net.loginto.common.PlayerUtils.Tries;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.velocity.Utils.Files.LoginToFiles;
import net.loginto.common.Utils.Files.MessageKeys;

import java.util.Optional;

public class PlayerStatus {

    public static void setPlayerAsLogged(Player player, ProxyServer server) {

        Sessions.addPlayer(player.getUniqueId());

        Tries.resetTries(player.getUniqueId());

        RegisteredServer lobbyServer = PlayerStatus.Servers.getRegisteredLobbyServer(player, server);

        player.createConnectionRequest(lobbyServer).connect();
    }

    public static void setPlayerAsNotLogged(Player player, ProxyServer server) {

        Sessions.removePlayer(player.getUniqueId());

        RegisteredServer authServer = PlayerStatus.Servers.getRegisteredAuthServer(player, server);

        player.createConnectionRequest(authServer).connect();
    }

    public static void setPlayerAsLogged(PlayerChooseInitialServerEvent event, ProxyServer server) {
        Sessions.addPlayer(event.getPlayer().getUniqueId());

        Tries.resetTries(event.getPlayer().getUniqueId());

        event.setInitialServer(PlayerStatus.Servers.getRegisteredLobbyServer(event.getPlayer(), server));
    }

    public static void setPlayerAsNotLogged(PlayerChooseInitialServerEvent event, ProxyServer server) {
        Sessions.removePlayer(event.getPlayer().getUniqueId());

        event.setInitialServer(PlayerStatus.Servers.getRegisteredAuthServer(event.getPlayer(), server));
    }

    protected static class Servers {
        public static RegisteredServer getRegisteredAuthServer(Player player, ProxyServer server) {
            for (Object serverObject : LoginToFiles.Config.getList(ConfigKeys.SERVERS_AUTH_SERVERS.path())) {
                Optional<RegisteredServer> authServer = server.getServer(serverObject.toString());

                if (authServer.isPresent()) {
                    return authServer.get();
                }
            }
            player.disconnect(LoginToFiles.Messages.getMessageComponent(MessageKeys.SERVERS_AUTH_SERVERS_ERROR.path()));
            return null;
        }

        public static RegisteredServer getRegisteredLobbyServer(Player player, ProxyServer server) {
            for (Object serverObject : LoginToFiles.Config.getList(ConfigKeys.SERVER_DESTINATION_SERVERS.path())) {
                Optional<RegisteredServer> destinationServer = server.getServer(serverObject.toString());

                if (destinationServer.isPresent()) {
                    return destinationServer.get();
                }
            }
            player.disconnect(LoginToFiles.Messages.getMessageComponent(MessageKeys.SERVERS_DESTINATIONS_SERVERS_ERROR.path()));
            return null;
        }
    }
}

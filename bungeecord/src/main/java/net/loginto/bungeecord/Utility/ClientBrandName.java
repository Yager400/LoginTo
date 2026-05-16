/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Utility;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ClientBrandName {

    private final String brand;
    private final ProxiedPlayer player;
    private final ProxyServer server;

    public ClientBrandName(String brand, ProxiedPlayer player, ProxyServer server) {
        this.brand = brand;
        this.player = player;
        this.server = server;
    }
    
    public void check() {
        boolean isClientAllowed = false;

        List<String> clients = FileMGR.YamlReadList("banned-clients.allowed-client");

        for (String client : clients) {
            if (client.equals(brand)) {
                isClientAllowed = true;
                break;
            }
        }

        if (isClientAllowed) return;

        String action = FileMGR.YamlRead("banned-clients.action").trim();

        if (action.equalsIgnoreCase("FLAG")) {

            String message = FileMGR.YamlRead("banned-clients.flag-message");
            message = message.replace("%playerName%", player.getName());
            message = message.replace("%client%", brand);
            
            for (ProxiedPlayer p : server.getPlayers()) {
                if (p.hasPermission("loginto.flag-banned-client")) {
                    p.sendMessage(TextComponent.fromLegacyText(message));
                }
            }

            return;
        }

        if (action.equalsIgnoreCase("FLAG_AND_LOG")) {
            log(player, brand);

            String message = FileMGR.YamlRead("banned-clients.flag-message");
            message = message.replace("%playerName%", player.getName());
            message = message.replace("%client%", brand);
            
            for (ProxiedPlayer p : server.getPlayers()) {
                if (p.hasPermission("loginto.flag-banned-client")) {
                    p.sendMessage(TextComponent.fromLegacyText(message));
                }
            }

            return;
        }
    }
    

    private void log(ProxiedPlayer player, String brand) {
        JsonMenager json = new JsonMenager(new File("plugins", "loginto"), "clientBrandLog.json");
            try {
                if (!json.exists()) {
                    json.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            long timestamp = System.currentTimeMillis() / 1000;

            DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("dd/MM/yyyy HH:mm:ss")
                .withZone(ZoneId.systemDefault());

            json.set(timestamp + ".username", player.getName());
            json.set(timestamp + ".uuid", player.getUniqueId());
            json.set(timestamp + ".brand", brand);
            json.set(timestamp + ".formatted-time", formatter.format(Instant.ofEpochSecond(timestamp)));

            json.save();

    }
}

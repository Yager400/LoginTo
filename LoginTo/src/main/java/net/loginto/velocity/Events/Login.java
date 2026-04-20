/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.velocity.Events;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import net.kyori.adventure.text.Component;
import net.loginto.velocity.Utility.FileMGR;
import net.loginto.velocity.Utility.JsonMenager;

public class Login {

    private final ProxyServer server;

    public Login(ProxyServer server) {
        this.server = server;
    }
    
    @Subscribe
    public void onPlayerLogin(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        String brand = player.getClientBrand();

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
            message = message.replace("%playerName%", player.getUsername());
            message = message.replace("%client%", brand);
            
            for (Player p : server.getAllPlayers()) {
                if (p.hasPermission("loginto.flag-banned-client")) {
                    p.sendMessage(Component.text(message));
                }
            }

            return;
        }

        if (action.equalsIgnoreCase("FLAG_AND_LOG")) {
            log(player, brand);

            String message = FileMGR.YamlRead("banned-clients.flag-message");
            message = message.replace("%playerName%", player.getUsername());
            message = message.replace("%client%", brand);
            
            for (Player p : server.getAllPlayers()) {
                if (p.hasPermission("loginto.flag-banned-client")) {
                    p.sendMessage(Component.text(message));
                }
            }

            return;
        }

    }

    private void log(Player player, String brand) {
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

            json.set(timestamp + ".username", player.getUsername());
            json.set(timestamp + ".uuid", player.getUniqueId());
            json.set(timestamp + ".brand", brand);
            json.set(timestamp + ".formatted-time", formatter.format(Instant.ofEpochSecond(timestamp)));

            json.save();

    }

}

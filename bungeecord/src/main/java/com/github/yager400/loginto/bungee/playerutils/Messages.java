/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bungee.playerutils;

import com.github.yager400.loginto.bungee.LoginTo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.HashMap;

public class Messages {

    private static final MiniMessage mm = MiniMessage.miniMessage();

    private static String getLegacyString(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    private static String formatMessage(String message, HashMap<String, String> stringReplacement) {
        // LoginTo replacement
        if (stringReplacement != null) {
            for (String placeholder : stringReplacement.keySet()) {
                message = message.replace(placeholder, stringReplacement.get(placeholder));
            }
        }
        return message;
    }

    private static Component getMessageAsComponent(String message) {
        return mm.deserialize(message);
    }

    private static String[] getTitleAndSubTitle(String message) {
        message = message.substring("<title>".length());
        String[] split = message.split("<subtitle>", 2);
        String title = split[0];
        String subtitle = split.length > 1 ? split[1] : "";
        return new String[]{title, subtitle};
    }

    public static String getLegacyFormattedMessage(String message, HashMap<String, String> placeholders) {
        Component component = getMessageAsComponent(formatMessage(message, placeholders));
        return getLegacyString(component);
    }

    public static String getLegacyFormattedMessage(String message) {
        Component component = getMessageAsComponent(message);
        return getLegacyString(component);
    }

    public static class player {

        // Secure is used during player messaging during early events, like the first ServerConnectEvent, where the player
         // is not fully connected, causing them not to receive the message
        public static void sendText(String message, ProxiedPlayer player, HashMap<String, String> stringReplacement, boolean secure) {
            LoginTo.getInstance().getProxy().getScheduler().runAsync(LoginTo.getInstance(), () -> {
                while (!player.isConnected()) {
                    try { Thread.sleep(100); }
                    catch (Exception e) { return; }
                }

                // Wait 2 more seconds to give the player time to fully connect and receive the messages
                if (secure) {
                    try { Thread.sleep(2000); }
                    catch (Exception e) { return; }
                }

                if (message.startsWith("<title>")) {
                    sendTitle(message, player, stringReplacement);
                } else {
                    sendMessage(message, player, stringReplacement);
                }
            });
        }

        private static void sendMessage(String message, ProxiedPlayer player, HashMap<String, String> stringReplacement) {
            if (player == null) {
                return;
            }
            message = formatMessage(message, stringReplacement);
            if (player.getPendingConnection().getVersion() >= ProtocolConstants.MINECRAFT_1_16) {
                LoginTo.getAdventure().player(player).sendMessage(getMessageAsComponent(message));
            } else {
                player.sendMessage(TextComponent.fromLegacyText(getLegacyString(getMessageAsComponent(message))));
            }
        }

        private static void sendTitle(String message, ProxiedPlayer player, HashMap<String, String> stringReplacement) {
            if (player == null) {
                return;
            }
            message = formatMessage(message, stringReplacement);
            String[] titleData = getTitleAndSubTitle(message);
            Title title = LoginTo.getInstance().getProxy().createTitle()
                    .title(TextComponent.fromLegacyText(getLegacyString(getMessageAsComponent(titleData[0]))))
                    .subTitle(TextComponent.fromLegacyText(getLegacyString(getMessageAsComponent(titleData[1]))))
                    .fadeIn(10)
                    .stay(70)
                    .fadeOut(20);
            title.send(player);
        }
    }

    public static class console {

        public static void sendMessage(String message, HashMap<String, String> stringReplacement) {
            message = formatMessage(message, stringReplacement);
            if (ProtocolConstants.SUPPORTED_VERSION_IDS.contains(735)) {
                LoginTo.getAdventure().console().sendMessage(getMessageAsComponent(message));
            } else {
                LoginTo.getInstance().getProxy().getConsole().sendMessage(TextComponent.fromLegacyText(getLegacyString(getMessageAsComponent(message))));
            }
        }

    }

    public static class sender {
        // Function for sending a message to a command sender (only used by commands since it's a CommandSender)
        public static void sendTextOrMessage(String message, CommandSender sender, HashMap<String, String> stringReplacement) {
            if (sender instanceof ProxiedPlayer player) {
                Messages.player.sendText(message, player, stringReplacement, false);
            } else {
                Messages.console.sendMessage(message, stringReplacement);
            }
        }
    }

}

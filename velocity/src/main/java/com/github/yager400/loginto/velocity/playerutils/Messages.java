/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.velocity.playerutils;

import com.github.yager400.loginto.velocity.LoginTo;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;

import java.time.Duration;
import java.util.HashMap;

public class Messages {

    private static final MiniMessage mm = MiniMessage.miniMessage();

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

    public static Component getKickMessage(String message) {
        message = formatMessage(message, null);
        return getMessageAsComponent(message);
    }

    public static class player {
        public static void sendText(String message, Player player, HashMap<String, String> stringReplacement, boolean secure) {
            LoginTo.getServer().getScheduler().buildTask(LoginTo.getInstance(), () -> {
                while (!player.isActive()) {
                    try { Thread.sleep(100); }
                    catch (Exception e) { return; }
                }

                if (secure) {
                    try { Thread.sleep(1000); }
                    catch (Exception e) { return; }
                }

                if (message.startsWith("<title>")) {
                    sendTitle(message, player, stringReplacement);
                } else {
                    sendMessage(message, player, stringReplacement);
                }
            }).schedule();
        }

        private static void sendMessage(String message, Player player, HashMap<String, String> stringReplacement) {
            if (player == null) {
                return;
            }
            message = formatMessage(message, stringReplacement);
            player.sendMessage(getMessageAsComponent(message));
        }

        private static void sendTitle(String message, Player player, HashMap<String, String> stringReplacement) {
            if (player == null) {
                return;
            }
            message = formatMessage(message, stringReplacement);
            String[] titleData = getTitleAndSubTitle(message);
            Title.Times times = Title.Times.times(
                    Duration.ofMillis(200),
                    Duration.ofMillis(1400),
                    Duration.ofMillis(400));
            Title title = Title.title(getMessageAsComponent(titleData[0]), getMessageAsComponent(titleData[1]), times);
            player.showTitle(title);
        }
    }

    public static class console {
        public static void sendMessage(String message, HashMap<String, String> stringReplacement) {
            message = formatMessage(message, stringReplacement);
            LoginTo.getServer().getConsoleCommandSource().sendMessage(getMessageAsComponent(message));
        }
    }

    public static class sender {
        // Function for sending a message to a command source (only used by commands since it's a CommandSource)
        public static void sendTextOrMessage(String message, CommandSource sender, HashMap<String, String> stringReplacement) {
            if (sender instanceof Player player) {
                Messages.player.sendText(message, player, stringReplacement, false);
            } else {
                console.sendMessage(message, stringReplacement);
            }
        }
    }

}

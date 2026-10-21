/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.playerutils;

import com.github.yager400.loginto.bukkit.LoginTo;
import com.github.yager400.loginto.bukkit.playerutils.kyorihandlers.KyoriHandler;
import com.github.yager400.loginto.bukkit.playerutils.kyorihandlers.LegacyKyoriHandler;
import com.github.yager400.loginto.bukkit.playerutils.kyorihandlers.NativeKyoriHandler;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Messages {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static KyoriHandler kyoriHandler;
    static {
        try {
            kyoriHandler = new NativeKyoriHandler();
            LoginTo.getInstance().getLogger().info("Using paper's native kyori library.");
        } catch (Exception e) {
            kyoriHandler = new LegacyKyoriHandler();
            LoginTo.getInstance().getLogger().warning("Using legacy kyori library, some tag might not render as excepted. Upgrade to a modern version of paper to fix this.");
        }
    }

    private static String getLegacyString(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    private static String formatMessage(String message, Player player, HashMap<String, String> stringReplacement) {
        // PlaceholderAPI replacement
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            if (player != null) {
                message = PlaceholderAPI.setPlaceholders(player, message);
            }
        }
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

    public static String getLegacyFormattedMessage(String message, Player player, HashMap<String, String> placeholders) {
        Component component = getMessageAsComponent(formatMessage(message, player, placeholders));
        return getLegacyString(component);
    }

    public static String getLegacyFormattedMessage(String message) {
        Component component = getMessageAsComponent(message);
        return getLegacyString(component);
    }

    public static class player {

        public static void sendText(String message, Player player, HashMap<String, String> stringReplacement) {
            // Run this with the folia lib since the player might not be in the thread we are calling this function
            LoginTo.getFoliaLib().getScheduler().runAtEntity(player, wrappedTask -> {
                if (message.startsWith("<title>")) {
                    sendTitle(message, player, stringReplacement);
                } else {
                    String formattedMessage = formatMessage(message, player, stringReplacement);
                    kyoriHandler.sendMessage(player, formattedMessage);
                }
            });
        }

        private static void sendTitle(String message, Player player, HashMap<String, String> stringReplacement) {
            if (player == null) {
                return;
            }
            message = formatMessage(message, player, stringReplacement);
            String[] titleData = getTitleAndSubTitle(message);
            player.sendTitle(
                    getLegacyString(getMessageAsComponent(titleData[0])),
                    getLegacyString(getMessageAsComponent(titleData[1])),
                    10,
                    70,
                    20
            );
        }
    }

    public static class sender {
        // Function for sending a message to a command sender (only used by commands since it's a CommandSender)
        public static void sendTextOrMessage(String message, CommandSender sender, HashMap<String, String> stringReplacement) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Messages.player.sendText(message, player, stringReplacement);
            } else {
                String formattedMessage = formatMessage(message, null, stringReplacement);
                kyoriHandler.sendConsole(formattedMessage);
            }
        }
    }
}

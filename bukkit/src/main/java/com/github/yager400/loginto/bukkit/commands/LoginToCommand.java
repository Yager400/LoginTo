/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.commands;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.yager400.loginto.bukkit.LoginTo;
import com.github.yager400.loginto.bukkit.PluginSetup;
import com.github.yager400.loginto.bukkit.playerutils.Messages;
import com.github.yager400.loginto.bukkit.playerutils.PlayerStatus;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LoginToCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            Messages.sender.sendTextOrMessage("<gold>LoginTo admin command arguments\n" +
                    "-----\n" +
                    "<green>help <gold>Shows this message\n" +
                    "<green>reload <gold>Reload the plugin's file configurations\n" +
                    "-----",
                    sender,
                    null
            );
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                try {
                    LoginTo.getConfigReader().close();
                    LoginTo.getMessageReader().close();
                    LoginTo.getWebhookReader().close();
                    LoginTo.configReader = PluginSetup.getYamlRead(new File(LoginTo.getInstance().getDataFolder(), "config.yml"));
                    LoginTo.messageReader = PluginSetup.getYamlRead(new File(LoginTo.getInstance().getDataFolder(), "messages.yml"));
                    LoginTo.webhookReader = PluginSetup.getYamlRead(new File(LoginTo.getInstance().getDataFolder(), "webhooks.yml"));
                    LoginTo.getDatabase().close();
                    LoginTo.database = PluginSetup.initDatabase(LoginTo.getConfigReader());
                    Messages.sender.sendTextOrMessage("<green>Files and database reloaded!", sender, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    Messages.sender.sendTextOrMessage("<red>An error as occurred", sender, null);
                }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.add("help");
            list.add("reload");
        }

        return list;
    }
}

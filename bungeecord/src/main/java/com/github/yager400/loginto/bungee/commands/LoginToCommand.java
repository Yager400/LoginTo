/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bungee.commands;

import com.github.yager400.loginto.bungee.LoginTo;
import com.github.yager400.loginto.bungee.PluginSetup;
import com.github.yager400.loginto.bungee.fileskeys.ConfigKeys;
import com.github.yager400.loginto.bungee.fileskeys.MessagesKeys;
import com.github.yager400.loginto.bungee.playerutils.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoginToCommand extends Command implements TabExecutor {

    public LoginToCommand() {
        super("loginto", "loginto.loginto");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            Messages.sender.sendTextOrMessage("""
                    <gold>LoginTo admin command arguments
                    -----
                    <green>help <gold>Shows this message
                    <green>reload <gold>Reload the plugin's file configurations
                    -----
                    """,
                    sender,
                    null
            );
            return;
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
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.add("help");
            list.add("reload");
        }

        return list;
    }

}

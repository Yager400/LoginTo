/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.velocity.commands;

import com.github.yager400.loginto.velocity.LoginTo;
import com.github.yager400.loginto.velocity.PluginSetup;
import com.github.yager400.loginto.velocity.playerutils.Messages;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoginToCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
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
                    LoginTo.configReader = PluginSetup.getYamlRead(new File(LoginTo.getDataDirectory().toFile(), "config.yml"));
                    LoginTo.messageReader = PluginSetup.getYamlRead(new File(LoginTo.getDataDirectory().toFile(), "messages.yml"));
                    LoginTo.webhookReader = PluginSetup.getYamlRead(new File(LoginTo.getDataDirectory().toFile(), "webhooks.yml"));
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
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("loginto.loginto");
    }

    @Override
    public List<String> suggest(final Invocation invocation) {
        List<String> list = new ArrayList<>();
        String[] args = invocation.arguments();

        if (args.length == 1) {
            list.add("help");
            list.add("reload");
        }

        return list;
    }

}

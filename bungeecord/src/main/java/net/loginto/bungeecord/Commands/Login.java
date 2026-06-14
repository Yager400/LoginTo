/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Commands;

import net.loginto.bungeecord.PlayerUtils.PlayerStatus;
import net.loginto.bungeecord.Utils.Files.LoginToFiles;
import net.loginto.bungeecord.Utils.WebHooks;
import net.loginto.common.Database.Database;
import net.loginto.common.PlayerUtils.Sessions;
import net.loginto.common.PlayerUtils.Tries;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.common.Utils.Files.MessageKeys;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Login extends Command implements TabExecutor {

    private final Database database;
    private final ProxyServer server;
    private final Logger logger;

    public Login(Database database, ProxyServer server, Logger logger) {
        super("login", "loginto.login", "l");

        this.database = database;
        this.server = server;
        this.logger = logger;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer player)) {
            sender.sendMessage(ChatColor.RED + "Not a player");
            return;
        }

        if (args.length != 1) {
            player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.LOGIN_ERROR_USAGE.path()));
            return;
        }


        if (Sessions.isPlayerLogged(player.getUniqueId())) {
            player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.LOGIN_ERROR_ALREADY_LOGGED_IN.path()));
            return;
        }


        String password = args[0];

        if (!database.isPlayerPresentInDB(player.getName())) {
            player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.LOGIN_ERROR_NOT_REGISTERED.path()));
            return;
        }

        try {
            if (!database.isPasswordCorrect(player.getName(), password)) {
                player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.LOGIN_ERROR_WRONG_PASSWORD.path()));
                if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.AUTH_SECURITY_KICK_ON_INVALID_PASSWORD.path())) {
                    Tries.addTry(player.getUniqueId());
                    if (Tries.triesEnded(player.getUniqueId(), LoginToFiles.Config.getInt(ConfigKeys.AUTH_SECURITY_MAX_LOGIN_ATTEMPTS.path()))) {
                        player.disconnect(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.ERRORS_LOGIN_FAIL_ONKICK_FAILED_LOGIN.path()));
                    }
                }
            } else {
                player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.LOGIN_SUCCESS.path()));

                PlayerStatus.setPlayerAsLogged(player, server);

                WebHooks.login(player.getName(), logger);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            list.add("<password>");
            return list;
        }

        return List.of();
    }
}

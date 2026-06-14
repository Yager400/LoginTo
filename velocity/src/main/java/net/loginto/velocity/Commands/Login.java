/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.velocity.Commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.loginto.common.Database.Database;
import net.loginto.velocity.PlayerUtils.PlayerStatus;
import net.loginto.common.PlayerUtils.Sessions;
import net.loginto.common.PlayerUtils.Tries;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.velocity.Utils.Files.LoginToFiles;
import net.loginto.common.Utils.Files.MessageKeys;
import net.loginto.velocity.Utils.WebHooks;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Login implements SimpleCommand {

    private final Database database;
    private final ProxyServer server;
    private final Logger logger;

    public Login(Database database, ProxyServer server, Logger logger) {
        this.database = database;
        this.server = server;
        this.logger = logger;
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();

        String[] args = invocation.arguments();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Not a player", NamedTextColor.RED));
            return;
        }

        if (args.length != 1) {
            player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.LOGIN_ERROR_USAGE.path()));
            return;
        }


        if (Sessions.isPlayerLogged(player.getUniqueId())) {
            player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.LOGIN_ERROR_ALREADY_LOGGED_IN.path()));
            return;
        }


        String password = args[0];

        if (!database.isPlayerPresentInDB(player.getUsername())) {
            player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.LOGIN_ERROR_NOT_REGISTERED.path()));
            return;
        }

        try {
            if (!database.isPasswordCorrect(player.getUsername(), password)) {
                player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.LOGIN_ERROR_WRONG_PASSWORD.path()));
                if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.AUTH_SECURITY_KICK_ON_INVALID_PASSWORD.path())) {
                    Tries.addTry(player.getUniqueId());
                    if (Tries.triesEnded(player.getUniqueId(), LoginToFiles.Config.getInt(ConfigKeys.AUTH_SECURITY_MAX_LOGIN_ATTEMPTS.path()))) {
                        player.disconnect(LoginToFiles.Messages.getMessageComponent(MessageKeys.ERRORS_LOGIN_FAIL_ONKICK_FAILED_LOGIN.path()));
                    }
                }
            } else {
                player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.LOGIN_SUCCESS.path()));

                PlayerStatus.setPlayerAsLogged(player, server);

                WebHooks.login(player.getUsername(), logger);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("loginto.login");
    }

    @Override
    public List<String> suggest(final Invocation invocation) {

        if (invocation.arguments().length == 1) {
            List<String> list = new ArrayList<>();
            list.add("<password>");
            return list;
        }

        return List.of();
    }
}

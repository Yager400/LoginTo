/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.velocity.Commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.loginto.common.Database.Cache.PremiumSQLiteCache;
import net.loginto.common.Database.Database;
import net.loginto.common.Utils.PremiumUtils;
import net.loginto.velocity.LoginTo;
import net.loginto.velocity.PlayerUtils.PasswordSecurity;
import net.loginto.velocity.PlayerUtils.PlayerStatus;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.velocity.Utils.Files.LoginToFiles;
import net.loginto.common.Utils.Files.MessageKeys;
import net.loginto.velocity.Utils.WebHooks;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Register implements SimpleCommand {

    private final Database database;
    private final Logger logger;
    private final ProxyServer server;
    private final LoginTo plugin;
    private final PremiumSQLiteCache sqliteCache;

    public Register(Database database, Logger logger, ProxyServer server, LoginTo plugin, PremiumSQLiteCache sqliteCache) {
        this.database = database;
        this.logger = logger;
        this.server = server;
        this.plugin = plugin;
        this.sqliteCache = sqliteCache;
    }

    @Override
    public void execute(final Invocation invocation) {

        if (!(invocation.source() instanceof Player player)) {
            invocation.source().sendMessage(Component.text("Not a player", NamedTextColor.RED));
            return;
        }

        String[] args = invocation.arguments();

        if (args.length != 2) {
            player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.REGISTER_ERROR_USAGE.path()));
            return;
        }

        String password = args[0];

        String repetePassword = args[1];

        if (!password.equals(repetePassword)) {
            player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.REGISTER_ERROR_PASSWORD_MISMATCH.path()));
            return;
        }

        if (!PasswordSecurity.doesIncludeReqChars(password)) {
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%characters%", LoginToFiles.Config.getString(ConfigKeys.PASSWORD_REQUIREMENTS_REQUIRED_CHAR_LIST.path()));
            player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.REGISTER_ERROR_CHARACTER_ERROR.path(), placeholders));
            return;
        }

        if (!PasswordSecurity.matchesLengthRequirement(password, player)) {
            // The message get send in the PasswordSecurity.doesMatchLength function
            return;
        }

        server.getScheduler().buildTask(plugin, () -> {

            if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PASSWORD_REQUIREMENTS_BANNED_PASSWORD_DECLINE_ON_COMMON.path())) {
                if (PasswordSecurity.isCommon(password, player.getUsername(), logger)) {
                    player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.REGISTER_ERROR_PASSWORD_TOO_SIMPLE.path()));
                    return;
                }
            }

            if (database.isPlayerPresentInDB(player.getUsername())) {
                player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.REGISTER_ERROR_ALREADY_REGISTERED.path()));
                return;
            }

            try {
                database.insertPlayer(player.getUsername(), password);
                if (PremiumUtils.isUserNamePremium(player.getUsername(), sqliteCache)) {
                    database.chanceAccStatusToPremium(player.getUsername(), true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.REGISTER_SUCCESS.path()));

            PlayerStatus.setPlayerAsLogged(player, server);

            WebHooks.register(player.getUsername(), logger);

        }).schedule();
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("loginto.register");
    }

    @Override
    public List<String> suggest(final Invocation invocation) {

        if (invocation.arguments().length == 1) {
            List<String> list = new ArrayList<>();
            list.add("<password>");
            return list;
        }
        if (invocation.arguments().length == 2) {
            List<String> list = new ArrayList<>();
            list.add("<confirmPassword>");
            return list;
        }

        return List.of();
    }


}

/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Commands;

import net.loginto.bungeecord.PlayerUtils.PasswordSecurity;
import net.loginto.bungeecord.PlayerUtils.PlayerStatus;
import net.loginto.bungeecord.Utils.Files.LoginToFiles;
import net.loginto.bungeecord.Utils.WebHooks;
import net.loginto.common.Database.Cache.PremiumSQLiteCache;
import net.loginto.common.Database.Database;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.common.Utils.Files.MessageKeys;
import net.loginto.common.Utils.PremiumUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Register extends Command implements TabExecutor {

    private final Database database;
    private final Logger logger;
    private final ProxyServer server;
    private final Plugin plugin;
    private final PremiumSQLiteCache sqliteCache;

    public Register(Database database, Logger logger, ProxyServer server, Plugin plugin, PremiumSQLiteCache sqliteCache) {
        super("register", "loginto.register", "r");

        this.database = database;
        this.logger = logger;
        this.server = server;
        this.plugin = plugin;
        this.sqliteCache = sqliteCache;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer player)) {
            sender.sendMessage(ChatColor.RED + "Not a player");
            return;
        }

        if (args.length != 2) {
            player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.REGISTER_ERROR_USAGE.path()));
            return;
        }

        String password = args[0];

        String repetePassword = args[1];

        if (!password.equals(repetePassword)) {
            player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.REGISTER_ERROR_PASSWORD_MISMATCH.path()));
            return;
        }

        if (!PasswordSecurity.doesIncludeReqChars(password)) {
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%characters%", LoginToFiles.Config.getString(ConfigKeys.PASSWORD_REQUIREMENTS_REQUIRED_CHAR_LIST.path()));
            player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.REGISTER_ERROR_CHARACTER_ERROR.path(), placeholders));
            return;
        }

        if (!PasswordSecurity.matchesLengthRequirement(password, player)) {
            // The message get send in the PasswordSecurity.doesMatchLength function
            return;
        }

        server.getScheduler().runAsync(plugin, () -> {

            if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PASSWORD_REQUIREMENTS_BANNED_PASSWORD_DECLINE_ON_COMMON.path())) {
                if (PasswordSecurity.isCommon(password, player.getName(), logger)) {
                    player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.REGISTER_ERROR_PASSWORD_TOO_SIMPLE.path()));
                    return;
                }
            }

            if (database.isPlayerPresentInDB(player.getName())) {
                player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.REGISTER_ERROR_ALREADY_REGISTERED.path()));
                return;
            }

            try {
                database.insertPlayer(player.getName(), password);
                if (PremiumUtils.isUserNamePremium(player.getName(), sqliteCache)) {
                    database.chanceAccStatusToPremium(player.getName(), true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.REGISTER_SUCCESS.path()));

            PlayerStatus.setPlayerAsLogged(player, server);

            WebHooks.register(player.getName(), logger);

        });
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            list.add("<password>");
            return list;
        }
        if (args.length == 2) {
            List<String> list = new ArrayList<>();
            list.add("<confirmPassword>");
            return list;
        }

        return List.of();
    }

}

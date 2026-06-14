/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Commands;

import net.loginto.bungeecord.PlayerUtils.PasswordSecurity;
import net.loginto.bungeecord.Utils.Files.LoginToFiles;
import net.loginto.common.Database.Database;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.common.Utils.Files.MessageKeys;
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

public class ChangePassword extends Command implements TabExecutor {

    private final Plugin plugin;
    private final Database database;
    private final ProxyServer server;
    private final Logger logger;

    public ChangePassword(Plugin plugin, Database database, ProxyServer server, Logger logger) {
        super("changepassword", "loginto.changepassword");

        this.plugin = plugin;
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

        if (args.length != 2) {
            player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.CHANGEPASSWORD_ERROR_USAGE.path()));
            return;
        }

        String newPassword = args[0];
        String oldPassword = args[1];

        if (!PasswordSecurity.doesIncludeReqChars(newPassword)) {
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%characters%", LoginToFiles.Config.getString(ConfigKeys.PASSWORD_REQUIREMENTS_REQUIRED_CHAR_LIST.path()));
            player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.REGISTER_ERROR_CHARACTER_ERROR.path(), placeholders));
            return;
        }

        if (!PasswordSecurity.matchesLengthRequirement(newPassword, player)) {
            // The message get send in the PasswordSecurity.doesMatchLength function
            return;
        }

        server.getScheduler().runAsync(plugin, () -> {
            if (PasswordSecurity.isCommon(newPassword, player.getName(), logger)) {
                player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.REGISTER_ERROR_PASSWORD_TOO_SIMPLE.path()));
                return;
            }

            if (!database.isPlayerPresentInDB(player.getName())) {
                player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.CHANGEPASSWORD_ERROR_NOT_REGISTERED.path()));
                return;
            }

            try {
                if (!database.isPasswordCorrect(player.getName(), oldPassword)) {
                    player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.CHANGEPASSWORD_ERROR_OLD_PASSWORD_WRONG.path()));
                    return;
                }

                database.changePlayerPassword(player.getName(), newPassword);

                player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.CHANGEPASSWORD_PASSWORD_CHANGED.path()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            list.add("<newPassword>");
            return list;
        }
        if (args.length == 2) {
            List<String> list = new ArrayList<>();
            list.add("<otp_code>");
            return list;
        }

        return List.of();
    }
}

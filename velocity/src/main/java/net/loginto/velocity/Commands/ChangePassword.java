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
import net.loginto.common.Database.Database;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.common.Utils.Files.MessageKeys;
import net.loginto.velocity.LoginTo;
import net.loginto.velocity.PlayerUtils.PasswordSecurity;
import net.loginto.velocity.Utils.Files.LoginToFiles;
import org.slf4j.Logger;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChangePassword implements SimpleCommand {

    private final LoginTo plugin;
    private final Database database;
    private final ProxyServer server;
    private final Logger logger;

    public ChangePassword(LoginTo plugin, Database database, ProxyServer server, Logger logger) {
        this.plugin = plugin;
        this.database = database;
        this.server = server;
        this.logger = logger;
    }

    @Override
    public void execute(final SimpleCommand.Invocation invocation) {

        if (!(invocation.source() instanceof Player player)) {
            invocation.source().sendMessage(Component.text("Not a player", NamedTextColor.RED));
            return;
        }

        if (invocation.arguments().length != 2) {
            player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.CHANGEPASSWORD_ERROR_USAGE.path()));
            return;
        }

        String newPassword = invocation.arguments()[0];
        String oldPassword = invocation.arguments()[1];

        if (!PasswordSecurity.doesIncludeReqChars(newPassword)) {
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%characters%", LoginToFiles.Config.getString(ConfigKeys.PASSWORD_REQUIREMENTS_REQUIRED_CHAR_LIST.path()));
            player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.REGISTER_ERROR_CHARACTER_ERROR.path(), placeholders));
            return;
        }

        if (!PasswordSecurity.matchesLengthRequirement(newPassword, player)) {
            // The message get send in the PasswordSecurity.doesMatchLength function
            return;
        }

        server.getScheduler().buildTask(plugin, () -> {
            if (PasswordSecurity.isCommon(newPassword, player.getUsername(), logger)) {
                player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.REGISTER_ERROR_PASSWORD_TOO_SIMPLE.path()));
                return;
            }

            if (!database.isPlayerPresentInDB(player.getUsername())) {
                player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.CHANGEPASSWORD_ERROR_NOT_REGISTERED.path()));
                return;
            }

            try {
                if (!database.isPasswordCorrect(player.getUsername(), oldPassword)) {
                    player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.CHANGEPASSWORD_ERROR_OLD_PASSWORD_WRONG.path()));
                    return;
                }

                database.changePlayerPassword(player.getUsername(), newPassword);

                player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.CHANGEPASSWORD_PASSWORD_CHANGED.path()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }).schedule();
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("loginto.changepassword");
    }

    @Override
    public List<String> suggest(final Invocation invocation) {

        if (invocation.arguments().length == 1) {
            List<String> list = new ArrayList<>();
            list.add("<newPassword>");
            return list;
        }
        if (invocation.arguments().length == 2) {
            List<String> list = new ArrayList<>();
            list.add("<otp_code>");
            return list;
        }

        return List.of();
    }
}

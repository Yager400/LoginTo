/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.velocity.commands;

import com.github.yager400.loginto.common.players.PlayerProtocolUtils;
import com.github.yager400.loginto.common.utils.WebHooks;
import com.github.yager400.loginto.velocity.LoginTo;
import com.github.yager400.loginto.velocity.fileskeys.ConfigKeys;
import com.github.yager400.loginto.velocity.fileskeys.MessagesKeys;
import com.github.yager400.loginto.velocity.playerutils.Messages;
import com.github.yager400.loginto.velocity.playerutils.PlayerStatus;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnregisterCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        if (args.length != 1) {
            Messages.sender.sendTextOrMessage("<white>Usage: /unregister <username>", sender, null);
            return;
        }

        if (sender instanceof Player player) {
            if (player.getUsername().equals(args[0])) {
                Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.UNREGISTER_CANNOTUNREGISTERYOURSELF), sender, null);
                return;
            }
        }
        UUID targetUUID;
        if (!LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PREMIUM_ENABLED)) {
            targetUUID = PlayerProtocolUtils.generateUUIDFromUsername(args[0]);
        } else {
            targetUUID = PlayerProtocolUtils.getUUIDFromName(args[0], LoginTo.getConfigReader().getList(ConfigKeys.SETTINGS_PREMIUM_PREMIUMBYPASSLIST), LoginTo.getPremiumCache());
        }

        if (!LoginTo.getDatabase().databaseContainsPlayer(targetUUID)) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.UNREGISTER_INVALIDPLAYER), sender, null);
            return;
        }

        LoginTo.getDatabase().removePlayerRecord(targetUUID);

        Player targetPlayer = LoginTo.getServer().getPlayer(targetUUID).orElse(null);

        if (targetPlayer != null) {
            targetPlayer.disconnect(Messages.getKickMessage(LoginTo.getMessageReader().getString(MessagesKeys.UNREGISTER_UNREGISTERSUCCESS)));
        }

        Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.UNREGISTER_ADMINUNREGISTERSUCCESS), sender, null);

        if (sender instanceof Player player) {
            WebHooks.sendUnRegisterWebhook(player.getUsername(), player.getUniqueId(), args[0], targetUUID);
        } else {
            WebHooks.sendUnRegisterWebhook("Console", new UUID(0L, 0L), args[0], targetUUID);
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("loginto.unregister");
    }

    @Override
    public List<String> suggest(final Invocation invocation) {
        List<String> list = new ArrayList<>();
        String[] args = invocation.arguments();

        if (args.length == 1) {
            for (Player player : LoginTo.getServer().getAllPlayers()) {
                list.add(player.getUsername());
            }
        }

        return list;
    }

}

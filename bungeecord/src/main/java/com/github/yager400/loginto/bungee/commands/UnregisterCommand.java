/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bungee.commands;

import com.github.yager400.loginto.bungee.LoginTo;
import com.github.yager400.loginto.bungee.fileskeys.ConfigKeys;
import com.github.yager400.loginto.bungee.fileskeys.MessagesKeys;
import com.github.yager400.loginto.bungee.playerutils.Messages;
import com.github.yager400.loginto.common.players.PlayerProtocolUtils;
import com.github.yager400.loginto.common.utils.WebHooks;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnregisterCommand extends Command implements TabExecutor {

    public UnregisterCommand() {
        super("unregister", "loginto.unregister", "delacc");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            Messages.sender.sendTextOrMessage("<white>Usage: /unregister <username>", sender, null);
            return;
        }

        if (sender instanceof ProxiedPlayer player) {
            if (player.getName().equals(args[0])) {
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

        ProxiedPlayer targetPlayer = LoginTo.getInstance().getProxy().getPlayer(targetUUID);

        if (targetPlayer != null) {
            targetPlayer.disconnect(TextComponent.fromLegacyText(Messages.getLegacyFormattedMessage(LoginTo.getMessageReader().getString(MessagesKeys.UNREGISTER_UNREGISTERSUCCESS))));
        }

        Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.UNREGISTER_ADMINUNREGISTERSUCCESS), sender, null);

        if (sender instanceof ProxiedPlayer player) {
            WebHooks.sendUnRegisterWebhook(player.getName(), player.getUniqueId(), args[0], targetUUID);
        } else {
            WebHooks.sendUnRegisterWebhook("Console", new UUID(0L, 0L), args[0], targetUUID);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            for (ProxiedPlayer player : LoginTo.getInstance().getProxy().getPlayers()) {
                list.add(player.getName());
            }
        }

        return list;
    }

}

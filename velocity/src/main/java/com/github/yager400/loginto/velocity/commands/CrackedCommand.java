/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.velocity.commands;

import com.github.yager400.loginto.common.players.PlayerProtocolUtils;
import com.github.yager400.loginto.velocity.LoginTo;
import com.github.yager400.loginto.velocity.fileskeys.ConfigKeys;
import com.github.yager400.loginto.velocity.fileskeys.MessagesKeys;
import com.github.yager400.loginto.velocity.playerutils.Messages;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CrackedCommand implements SimpleCommand {

    Set<Player> warnedPlayers = new HashSet<>();

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        if (args.length >= 1) {
            if (sender.hasPermission("loginto.cracked.other")) {
                List<?> premiumAuthBypass = LoginTo.getConfigReader().getList(ConfigKeys.SETTINGS_PREMIUM_PREMIUMBYPASSLIST);
                final UUID playerUUID;
                if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PREMIUM_ENABLED)) {
                    playerUUID = PlayerProtocolUtils.getUUIDFromName(args[0], premiumAuthBypass, LoginTo.getPremiumCache());
                } else {
                    playerUUID = PlayerProtocolUtils.generateUUIDFromUsername(args[0]);
                }
                if (LoginTo.getDatabase().databaseContainsPlayer(playerUUID)) {
                    CompletableFuture.runAsync(() -> {
                        LoginTo.getDatabase().updatePremium(playerUUID, false);
                        LoginTo.getDatabase().updateCracked(playerUUID, true);
                        Messages.sender.sendTextOrMessage("<green>This player is now cracked", sender, null);
                    });
                } else {
                    Messages.sender.sendTextOrMessage("<red>Player not registered", sender, null);
                }
                return;
            }
            Messages.sender.sendTextOrMessage("<white>Usage: /cracked [optional]<username>", sender, null);
            return;
        }

        if (!(sender instanceof Player player)) {
            Messages.console.sendMessage("<red>Not a player", null);
            return;
        }

        if (!warnedPlayers.contains(player)) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_CRACKED_CRACKEDWARN), sender, null);
            warnedPlayers.add(player);
            return;
        } else {
            warnedPlayers.remove(player);
        }

        if (LoginTo.getDatabase().isCracked(player.getUniqueId())) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_CRACKED_ALREADYCRACKED), sender, null);
            return;
        }

        CompletableFuture.runAsync(() -> {
            LoginTo.getDatabase().updatePremium(player.getUniqueId(), false);
            LoginTo.getDatabase().updateCracked(player.getUniqueId(), true);

            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_CRACKED_CRACKEDSUCCESS), sender, null);
        });
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("loginto.cracked.me");
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

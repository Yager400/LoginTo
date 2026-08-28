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
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PremiumCommand extends Command implements TabExecutor {

    Set<ProxiedPlayer> warnedPlayers = new HashSet<>();

    public PremiumCommand() {
        super("premium", "loginto.premium.me");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission("loginto.premium.other")) {
                List<?> premiumAuthBypass = LoginTo.getConfigReader().getList(ConfigKeys.SETTINGS_PREMIUM_PREMIUMBYPASSLIST);
                final UUID playerUUID;
                if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PREMIUM_ENABLED)) {
                    playerUUID = PlayerProtocolUtils.getUUIDFromName(args[0], premiumAuthBypass, LoginTo.getPremiumCache());
                } else {
                    playerUUID = PlayerProtocolUtils.generateUUIDFromUsername(args[0]);
                }
                if (LoginTo.getDatabase().databaseContainsPlayer(playerUUID)) {
                    CompletableFuture.runAsync(() -> {
                        LoginTo.getDatabase().updatePremium(playerUUID, true);
                        LoginTo.getDatabase().updateCracked(playerUUID, false);
                        Messages.sender.sendTextOrMessage("<green>This player is now premium", sender, null);
                    });
                } else {
                    Messages.sender.sendTextOrMessage("<red>Player not registered", sender, null);
                }
                return;
            }
            Messages.sender.sendTextOrMessage("<white>Usage: /premium [optional]<username>", sender, null);
            return;
        }

        if (!(sender instanceof ProxiedPlayer player)) {
            Messages.sender.sendTextOrMessage("<red>Not a player", sender, null);
            return;
        }

        if (!warnedPlayers.contains(player)) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_PREMIUM_PREMIUMWARN), sender, null);
            warnedPlayers.add(player);
            return;
        } else {
            warnedPlayers.remove(player);
        }

        if (LoginTo.getDatabase().isPremium(player.getUniqueId())) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_PREMIUM_ALREADYPREMIUM), sender, null);
            return;
        }

        CompletableFuture.runAsync(() -> {
            LoginTo.getDatabase().updatePremium(player.getUniqueId(), true);
            LoginTo.getDatabase().updateCracked(player.getUniqueId(), false);

            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_PREMIUM_PREMIUMSUCCESS), sender, null);
        });
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

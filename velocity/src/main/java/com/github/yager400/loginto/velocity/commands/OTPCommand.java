/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.velocity.commands;

import com.github.yager400.loginto.common.utils.OTPCodeUtils;
import com.github.yager400.loginto.velocity.LoginTo;
import com.github.yager400.loginto.velocity.fileskeys.MessagesKeys;
import com.github.yager400.loginto.velocity.playerutils.Messages;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import java.util.*;

public class OTPCommand implements SimpleCommand {

    Set<Player> alertedPlayers = new HashSet<>();

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();

        if (!(sender instanceof Player)) {
            Messages.sender.sendTextOrMessage("<red>Not a player", sender, null);
            return;
        }

        Player player = (Player) invocation.source();

        String storedSecret = LoginTo.getDatabase().getSecret(player.getUniqueId());

        if (storedSecret != null && !storedSecret.isEmpty()) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.OTP_OTPALREADYCREATED), sender, null);
            return;
        }

        if (!alertedPlayers.contains(player)) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.OTP_OTPALERTURL), sender, null);
            alertedPlayers.add(player);
            return;
        }
        alertedPlayers.remove(player);

        GoogleAuthenticatorKey key = OTPCodeUtils.getRandomKey();

        LoginTo.getDatabase().updateSecret(player.getUniqueId(), key.getKey());

        String otpData = OTPCodeUtils.getOtpUrl(player.getUsername(), "LoginTo-AUTH", key);

        String formattedURL = OTPCodeUtils.getQRCodeServerUrl(otpData);
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%otp_url%", formattedURL);
        Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.OTP_SENDURL), sender, placeholders);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("loginto.otp");
    }

    @Override
    public List<String> suggest(final Invocation invocation) {
        List<String> list = new ArrayList<>();

        return list;
    }
}

/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bungee.commands;

import com.github.yager400.loginto.bungee.LoginTo;
import com.github.yager400.loginto.bungee.fileskeys.MessagesKeys;
import com.github.yager400.loginto.bungee.playerutils.Messages;
import com.github.yager400.loginto.common.utils.OTPCodeUtils;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;

public class OTPCommand extends Command implements TabExecutor {

    Set<ProxiedPlayer> alertedPlayers = new HashSet<>();

    public OTPCommand() {
        super("otp", "loginto.otp");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            Messages.sender.sendTextOrMessage("<red>Not a player", sender, null);
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

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

        String otpData = OTPCodeUtils.getOtpUrl(player.getName(), "LoginTo-AUTH", key);

        String formattedURL = OTPCodeUtils.getQRCodeServerUrl(otpData);
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%otp_url%", formattedURL);
        Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.OTP_SENDURL), sender, placeholders);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        return list;
    }
}

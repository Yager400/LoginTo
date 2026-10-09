/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.commands;

import com.github.yager400.loginto.bukkit.LoginTo;
import com.github.yager400.loginto.bukkit.fileskeys.ConfigKeys;
import com.github.yager400.loginto.bukkit.fileskeys.MessagesKeys;
import com.github.yager400.loginto.bukkit.playerutils.Messages;
import com.github.yager400.loginto.bukkit.playerutils.OTPCodeMapUtils;
import com.github.yager400.loginto.common.utils.OTPCodeUtils;
import com.google.zxing.common.BitMatrix;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class OTPCommand implements CommandExecutor, TabCompleter {

    Set<Player> alertedPlayers = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            Messages.sender.sendTextOrMessage("<red>Not a player", sender, null);
            return true;
        }

        Player player = (Player) sender;

        String storedSecret = LoginTo.getDatabase().getSecret(player.getUniqueId());

        if (storedSecret != null && !storedSecret.isEmpty()) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.OTP_OTPALREADYCREATED), sender, null);
            return true;
        }

        if (!alertedPlayers.contains(player)) {
            if (LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_OTP_OTPTYPE).equalsIgnoreCase("MAP")) {
                Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.OTP_MAP_OTPALERTMAP), sender, null);
            } else {
                Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.OTP_URL_OTPALERTURL), sender, null);
            }
            alertedPlayers.add(player);
            return true;
        }
        alertedPlayers.remove(player);

        GoogleAuthenticatorKey key = OTPCodeUtils.getRandomKey();

        LoginTo.getDatabase().updateSecret(player.getUniqueId(), key.getKey());

        String otpData = OTPCodeUtils.getOtpUrl(player.getName(), "LoginTo-AUTH", key);

        if (LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_OTP_OTPTYPE).equalsIgnoreCase("MAP")) {
            // For minecraft's map, use 128x128px for the qrcode
            BitMatrix matrix = OTPCodeUtils.getBitMatrix(otpData, 128);
            OTPCodeMapUtils.handleMapCreationAndDeletion(matrix, player);
        } else {
            String formattedURL = OTPCodeUtils.getQRCodeServerUrl(otpData);
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%otp_url%", formattedURL);
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.OTP_URL_OTPSENDURL), sender, placeholders);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();

        return list;
    }

}

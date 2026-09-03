/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.commands;

import com.github.yager400.loginto.bukkit.LoginTo;
import com.github.yager400.loginto.bukkit.fileskeys.MessagesKeys;
import com.github.yager400.loginto.bukkit.playerutils.Messages;
import com.github.yager400.loginto.bukkit.playerutils.OTPCodeMapUtils;
import com.google.zxing.common.BitMatrix;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.OTP_OTPALERT), sender, null);
            alertedPlayers.add(player);
            return true;
        }
        alertedPlayers.remove(player);

        GoogleAuthenticatorKey key = OTPCodeMapUtils.getRandomKey();

        LoginTo.getDatabase().updateSecret(player.getUniqueId(), key.getKey());

        BitMatrix matrix = OTPCodeMapUtils.getBitMatrix(player.getName(), "LoginTo-AUTH", key);

        OTPCodeMapUtils.handleMapCreationAndDeletion(matrix, player);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();

        return list;
    }

}

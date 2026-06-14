/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Commands;

import net.loginto.bungeecord.Utils.Files.LoginToFiles;
import net.loginto.common.Database.Database;
import net.loginto.common.Utils.Files.MessageKeys;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cracked extends Command {

    private final Database database;
    private final List<UUID> playersToWarn = new ArrayList<>();

    public Cracked(Database database) {
        super("cracked", "loginto.cracked");

        this.database = database;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer player)) {
            sender.sendMessage(ChatColor.RED + "Not a player");
            return;
        }

        if (!database.isPremium(player.getName())) {
            player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.CRACKED_ERROR_ALREADY_CRACKED.path()));
            return;
        }

        if (!playersToWarn.contains(player.getUniqueId())) {
            player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.CRACKED_WARN.path()));
            playersToWarn.add(player.getUniqueId());
            return;
        }

        playersToWarn.remove(player.getUniqueId());

        database.chanceAccStatusToPremium(player.getName(), false);

        player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.CRACKED_DONE.path()));

    }
}

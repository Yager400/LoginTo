/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.velocity.Commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.loginto.common.Database.Database;
import net.loginto.velocity.Utils.Files.LoginToFiles;
import net.loginto.common.Utils.Files.MessageKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Premium implements SimpleCommand {

    private final Database database;
    private final List<UUID> playersToWarn = new ArrayList<>();

    public Premium(Database database) {
        this.database = database;
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Not a player", NamedTextColor.RED));
            return;
        }

        if (database.isPremium(player.getUsername())) {
            player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.PREMIUM_ERROR_ALREADY_PREMIUM.path()));
            return;
        }

        if (!playersToWarn.contains(player.getUniqueId())) {
            player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.PREMIUM_WARN.path()));
            playersToWarn.add(player.getUniqueId());
            return;
        }

        playersToWarn.remove(player.getUniqueId());

        database.chanceAccStatusToPremium(player.getUsername(), true);

        player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.PREMIUM_DONE.path()));
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("loginto.premium");
    }

    @Override
    public List<String> suggest(final Invocation invocation) {
        return List.of();
    }
}

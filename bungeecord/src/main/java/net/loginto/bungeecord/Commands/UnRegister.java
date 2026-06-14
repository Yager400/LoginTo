/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Commands;

import net.loginto.bungeecord.Utils.Files.LoginToFiles;
import net.loginto.bungeecord.Utils.WebHooks;
import net.loginto.common.Database.Cache.PremiumSQLiteCache;
import net.loginto.common.Database.Database;
import net.loginto.common.Utils.Files.MessageKeys;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class UnRegister extends Command implements TabExecutor {

    private final Database database;
    private final ProxyServer server;
    private final Logger logger;
    private final PremiumSQLiteCache sqLiteCache;

    public UnRegister(Database database, ProxyServer server, Logger logger, PremiumSQLiteCache sqLiteCache) {
        super("unregister", "loginto.unregister");

        this.server = server;
        this.database = database;
        this.logger = logger;
        this.sqLiteCache = sqLiteCache;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.UNREGISTER_ERROR_USAGE.path()));
            return;
        }

        if (!args[1].equalsIgnoreCase("confirm")) {
            sender.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.UNREGISTER_ERROR_NOT_CONFIRMED.path()));
            return;
        }

        ProxiedPlayer target = server.getPlayer(args[0]);

        if (!database.isPlayerPresentInDB(args[0])) {
            sender.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.UNREGISTER_ERROR_PLAYER_DOESNT_EXIST.path()));
            return;
        }

        boolean success = database.removePlayer(args[0]);

        if (!success) {
            sender.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.UNREGISTER_ERROR_PLAYER_DOESNT_EXIST.path()));
            return;
        }

        //Delete after
        sqLiteCache.deleteCacheRecord(args[0]);

        if (target != null) {
            target.disconnect(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.UNREGISTER_ADMIN_UNREGISTERED_ACCOUNT.path()));
        }

        sender.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.UNREGISTER_ACCOUNT_UNREGISTERED.path()));

        if (sender instanceof ProxiedPlayer player) {
            WebHooks.unregister(player.getName(), args[0], logger);
        } else {
            WebHooks.unregister("Console", args[0], logger);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            list.add("<player_name>");
            return list;
        }
        if (args.length == 2) {
            List<String> list = new ArrayList<>();
            list.add("<confirm>");
            return list;
        }

        return List.of();
    }
}

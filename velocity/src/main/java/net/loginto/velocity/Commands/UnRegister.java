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
import com.velocitypowered.api.proxy.ProxyServer;
import net.loginto.common.Database.Cache.PremiumSQLiteCache;
import net.loginto.common.Database.Database;
import net.loginto.velocity.Utils.Files.LoginToFiles;
import net.loginto.common.Utils.Files.MessageKeys;
import net.loginto.velocity.Utils.WebHooks;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class UnRegister implements SimpleCommand {

    private final Database database;
    private final ProxyServer server;
    private final Logger logger;
    private final PremiumSQLiteCache sqLiteCache;

    public UnRegister(Database database, ProxyServer server, Logger logger, PremiumSQLiteCache sqLiteCache) {
        this.server = server;
        this.database = database;
        this.logger = logger;
        this.sqLiteCache = sqLiteCache;
    }

    @Override
    public void execute(final Invocation invocation) {

        CommandSource source = invocation.source();

        String[] args = invocation.arguments();

        if (args.length != 2) {
            source.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.UNREGISTER_ERROR_USAGE.path()));
            return;
        }

        if (!args[1].equalsIgnoreCase("confirm")) {
            source.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.UNREGISTER_ERROR_NOT_CONFIRMED.path()));
            return;
        }

        Player target = server.getPlayer(args[0]).orElseGet(null);

        if (!database.isPlayerPresentInDB(args[0])) {
            source.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.UNREGISTER_ERROR_PLAYER_DOESNT_EXIST.path()));
            return;
        }

        boolean success = database.removePlayer(args[0]);

        if (!success) {
            source.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.UNREGISTER_ERROR_PLAYER_DOESNT_EXIST.path()));
            return;
        }

        //Delete after
        sqLiteCache.deleteCacheRecord(args[0]);

        if (target != null) {
            target.disconnect(LoginToFiles.Messages.getMessageComponent(MessageKeys.UNREGISTER_ADMIN_UNREGISTERED_ACCOUNT.path()));
        }

        source.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.UNREGISTER_ACCOUNT_UNREGISTERED.path()));

        if (source instanceof Player player) {
            WebHooks.unregister(player.getUsername(), args[0], logger);
        } else {
            WebHooks.unregister("Console", args[0], logger);
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("loginto.unregister");
    }

    @Override
    public List<String> suggest(final Invocation invocation) {

        if (invocation.arguments().length == 1) {
            List<String> list = new ArrayList<>();
            list.add("<player_name>");
            return list;
        }
        if (invocation.arguments().length == 2) {
            List<String> list = new ArrayList<>();
            list.add("<confirm>");
            return list;
        }

        return List.of();
    }
}

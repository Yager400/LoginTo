/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Events.Listener;

import net.loginto.bukkit.PlayerUtils.PlayerMessages;
import net.loginto.bukkit.PlayerUtils.PlayerStatus;
import net.loginto.bukkit.PlayerUtils.Sessions;
import net.loginto.bukkit.PlayerUtils.Tries;
import net.loginto.bukkit.Database.Database;
import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import net.loginto.bukkit.Utils.Files.MessageKeys;
import net.loginto.bukkit.Utils.Premium.AuthenticatedPlayer;
import net.loginto.bukkit.Utils.Premium.ProtocolUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class onJoinEvent implements Listener {

    private final Plugin plugin;
    private final Database database;

    public onJoinEvent(Plugin plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        plugin.reloadConfig();

        String watermark = (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PLUGIN_UTILITY_SHOW_WATERMARK.path(), plugin)) ? " - Service offered by LoginTo on Modrinth" : "";

        PlayerStatus.setPlayerAsNotLogged(event.getPlayer(), plugin);

        //Premium authentication

        if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PREMIUM_ENABLE_PREMIUM_FEATURES.path(), plugin)) {

            AuthenticatedPlayer authenticatedPlayer = ProtocolUtils.authenticatedPlayer.get(event.getPlayer().getUniqueId());

            //If the player has a cracked account that bypass the authentication
            if (authenticatedPlayer == null) {
                PlayerMessages.player.kickPlayer(MessageKeys.ERRORS_LOGIN_FAIL_ON_AUTHENTICATION_SKIP.path(), event.getPlayer(), plugin);
                plugin.getLogger().warning(String.format("The player %s tried to join the server with a premium name and without authenticating.", event.getPlayer().getName()));
                return;
            }

            //If the player is from floodgate
            if (authenticatedPlayer.isBedrock) {
                bukkitPremiumAuthHandlers.handleBedrock(database, authenticatedPlayer, plugin, event, watermark);
                return;
            }

            //If the player did the authentication via mojang (using the plugin's system)
            if (authenticatedPlayer.isPremium) {
                bukkitPremiumAuthHandlers.handlePremium(database, authenticatedPlayer, plugin, event, watermark);
                return;
            }

            //If the player did the plugin's authentication, but his name is not premium or the mojang session endpoint responded with an error
            bukkitPremiumAuthHandlers.handleCracked(database, authenticatedPlayer, plugin, event, watermark);
            return;
        }

        //No premium authentication

        if (JoinUtil.firstTimeUtils(database, event.getPlayer(), event, plugin, watermark)) {
            return;
        }

        PlayerMessages.player.sendMessage(MessageKeys.LOGIN_PROMPT.path(), event.getPlayer(), plugin);
        JoinUtil.startCounter(event, plugin, false);
        Tries.insertPlayerWithZeroTries(event.getPlayer());

    }

    private static class bukkitPremiumAuthHandlers {
        private static void handleBedrock(Database database, AuthenticatedPlayer authenticatedPlayer, Plugin plugin, PlayerJoinEvent event, String watermark) {
            org.geysermc.floodgate.api.player.FloodgatePlayer floodgatePlayer = org.geysermc.floodgate.api.FloodgateApi.getInstance().getPlayer(authenticatedPlayer.playerUUID);

            if (floodgatePlayer == null) {
                PlayerMessages.player.kickPlayer(MessageKeys.ERRORS_LOGIN_FAIL_JAVA_PLAYER_MARKED_AS_BEDROCK.path(), event.getPlayer(), plugin);
                return;
            }

            if (JoinUtil.firstTimeUtils(database, event.getPlayer(), event, plugin, watermark)) {
                return;
            }

            PlayerMessages.player.sendMessage(MessageKeys.LOGIN_BEDROCK_SUCCESS.path(), event.getPlayer(), plugin);
            PlayerStatus.setPlayerAsLogged(event.getPlayer(), plugin, true, true);

        }

        private static void handlePremium(Database database, AuthenticatedPlayer authenticatedPlayer, Plugin plugin, PlayerJoinEvent event, String watermark) {

            Player player = Bukkit.getPlayer(authenticatedPlayer.playerUUID);

            if (player == null) {
                PlayerMessages.player.kickPlayer(MessageKeys.ERRORS_LOGIN_FAIL_ONLOGIN_WITH_DIFFERENT_UUID.path(), event.getPlayer(), plugin);
                return;
            }

            if (JoinUtil.firstTimeUtils(database, event.getPlayer(), event, plugin, watermark)) {
                return;
            }

            PlayerMessages.player.sendMessage(MessageKeys.LOGIN_PREMIUM_SUCCESS.path(), event.getPlayer(), plugin);
            PlayerStatus.setPlayerAsLogged(event.getPlayer(), plugin, true, true);
        }

        private static void handleCracked(Database database, AuthenticatedPlayer authenticatedPlayer, Plugin plugin, PlayerJoinEvent event, String watermark) {

            Player player = Bukkit.getPlayer(authenticatedPlayer.playerUUID);

            if (player == null) {
                PlayerMessages.player.kickPlayer(MessageKeys.ERRORS_LOGIN_FAIL_ONLOGIN_WITH_DIFFERENT_UUID.path(), event.getPlayer(), plugin);
                return;
            }

            if (JoinUtil.firstTimeUtils(database, player, event, plugin, watermark)) {
                return;
            }

            PlayerMessages.player.sendMessage(MessageKeys.LOGIN_PROMPT.path(), event.getPlayer(), plugin);
            JoinUtil.startCounter(event, plugin, false);
            Tries.insertPlayerWithZeroTries(event.getPlayer());
        }
    }

    private static class JoinUtil {

        public static boolean firstTimeUtils(Database database, Player player, PlayerJoinEvent event, Plugin plugin, String watermark) {
            boolean firstTimeInTheServer = !database.isPlayerPresentInDB(player.getName());
            if (firstTimeInTheServer) {
                JoinUtil.sendRegisterMessages(event, plugin, watermark);
                JoinUtil.startCounter(event, plugin, false);
                return true;
            }
            return false;
        }

        public static void sendRegisterMessages(PlayerJoinEvent event, Plugin plugin, String watermark) {
            if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PASSWORD_REQUIREMENTS_REQUIRE_SPECIAL_CHARS.path(), plugin)) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("%characters%", LoginToFiles.Config.getString(ConfigKeys.PASSWORD_REQUIREMENTS_REQUIRED_CHAR_LIST.path(), plugin));
                PlayerMessages.player.sendMessage(MessageKeys.REGISTER_PROMPT_CHARACTERS.path(), event.getPlayer(), plugin,placeholders);

            } else {
                PlayerMessages.player.sendMessage(MessageKeys.REGISTER_PROMPT.path(), event.getPlayer(), plugin);
            }
        }

        public static void startCounter(PlayerJoinEvent event, Plugin plugin, boolean isLogged) {
            if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.AUTH_SECURITY_KICK_ON_AUTH_TIMEOUT.path(), plugin)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!Sessions.isPlayerLogged(event.getPlayer()) && !isLogged) {
                            PlayerMessages.player.kickPlayer(MessageKeys.ERRORS_LOGIN_FAIL_ONKICK_LONG_WAITING.path(), event.getPlayer(), plugin);
                        }
                    }
                }.runTaskLater(plugin, LoginToFiles.Config.getInt(ConfigKeys.AUTH_SECURITY_AUTH_TIMEOUT_SECONDS.path(), plugin) * 20L);

            }
        }
    }
}

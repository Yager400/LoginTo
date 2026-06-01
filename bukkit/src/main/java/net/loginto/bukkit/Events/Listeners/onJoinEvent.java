/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Events.Listeners;

import net.loginto.bukkit.PlayerUtils.PlayerStatus;
import net.loginto.bukkit.PlayerUtils.Sessions;
import net.loginto.bukkit.PlayerUtils.Tries;
import net.loginto.bukkit.Storage.Database;
import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import net.loginto.bukkit.Utils.Files.MessageKeys;
import net.loginto.bukkit.Utils.Premium.bukkit.AuthenticatedPlayer;
import net.loginto.bukkit.Utils.Premium.bukkit.ProtocolUtils;
import net.loginto.bukkit.Utils.Premium.proxy.PremiumUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CompletableFuture;

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

        //Premium auth via proxy
        if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PREMIUM_ENABLE_PREMIUM_FEATURES.path(), plugin)) {
            proxyPremiumAuthHandler(event, watermark);
            return;
        }

        //Premium auth via bukkit
        if (LoginToFiles.Experimental.getExperimentalBoolean("premium.bukkit-premium-auth", plugin)) {

            AuthenticatedPlayer authenticatedPlayer = ProtocolUtils.authenticatedPlayer.get(event.getPlayer().getUniqueId());

            //If the player has a cracked account that bypass the authentication
            if (authenticatedPlayer == null) {
                event.getPlayer().kickPlayer(ChatColor.RED + LoginToFiles.Experimental.getExperimentalString("premium.player-messages.on-authentication-skip", plugin));
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

        //No premium auth

        if (JoinUtil.firstTimeUtils(database, event.getPlayer(), event, plugin, watermark)) {
            return;
        }

        event.getPlayer().sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.LOGIN_PROMPT.path(), event.getPlayer(), plugin));
        JoinUtil.startCounter(event, plugin, false);
        Tries.insertPlayerWithZeroTries(event.getPlayer());

    }

    private static class bukkitPremiumAuthHandlers {
        private static void handleBedrock(Database database, AuthenticatedPlayer authenticatedPlayer, Plugin plugin, PlayerJoinEvent event, String watermark) {
            org.geysermc.floodgate.api.player.FloodgatePlayer floodgatePlayer = org.geysermc.floodgate.api.FloodgateApi.getInstance().getPlayer(authenticatedPlayer.playerUUID);

            if (floodgatePlayer == null) {
                event.getPlayer().kickPlayer(ChatColor.RED + "You got marked as bedrock player, but it seems you are from java, try rejoining.");
                return;
            }

            if (JoinUtil.firstTimeUtils(database, event.getPlayer(), event, plugin, watermark)) {
                return;
            }

            event.getPlayer().sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.LOGIN_SUCCESS.path(), event.getPlayer(), plugin));
            PlayerStatus.setPlayerAsLogged(event.getPlayer(), plugin, true, true);

        }

        private static void handlePremium(Database database, AuthenticatedPlayer authenticatedPlayer, Plugin plugin, PlayerJoinEvent event, String watermark) {

            Player player = Bukkit.getPlayer(authenticatedPlayer.playerUUID);

            if (player == null) {
                event.getPlayer().kickPlayer(ChatColor.RED + "The authentication system and the server have 2 different UUID related to your account, try rejoining.");
                return;
            }

            if (JoinUtil.firstTimeUtils(database, event.getPlayer(), event, plugin, watermark)) {
                return;
            }

            event.getPlayer().sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.LOGIN_SUCCESS.path(), event.getPlayer(), plugin));
            PlayerStatus.setPlayerAsLogged(event.getPlayer(), plugin, true, true);
        }

        private static void handleCracked(Database database, AuthenticatedPlayer authenticatedPlayer, Plugin plugin, PlayerJoinEvent event, String watermark) {

            Player player = Bukkit.getPlayer(authenticatedPlayer.playerUUID);

            if (player == null) {
                event.getPlayer().kickPlayer(ChatColor.RED + "The authentication system and the server have 2 different UUID related to your account, try rejoining.");
                return;
            }

            if (JoinUtil.firstTimeUtils(database, player, event, plugin, watermark)) {
                return;
            }

            event.getPlayer().sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.LOGIN_PROMPT.path(), event.getPlayer(), plugin));
            JoinUtil.startCounter(event, plugin, false);
            Tries.insertPlayerWithZeroTries(event.getPlayer());
        }
    }

    private void proxyPremiumAuthHandler(PlayerJoinEvent event, String watermark) {

        boolean isLogged = Sessions.Proxy.isPlayerLoggedN(event.getPlayer(), plugin);

        if (!Sessions.isPlayerLogged(event.getPlayer()) && isLogged) {
            PlayerStatus.setPlayerAsLogged(event.getPlayer(), plugin, false, true);
            return;
        }

        PlayerStatus.setPlayerAsNotLogged(event.getPlayer(), plugin);

        if (JoinUtil.firstTimeUtils(database, event.getPlayer(), event, plugin, watermark)) {
            return;
        }

        boolean canAutoLogin = PremiumUtils.PlayerPremium.CheckIfAPlayerCanAutoLogin(event.getPlayer(), plugin);

        if (canAutoLogin || isLogged) {
            event.getPlayer().sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.LOGIN_SUCCESS.path(), event.getPlayer(), plugin));
            PlayerStatus.setPlayerAsLogged(event.getPlayer(), plugin, true, true);
        } else {
            event.getPlayer().sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.LOGIN_PROMPT.path(), event.getPlayer(), plugin));
            JoinUtil.startCounter(event, plugin, isLogged);
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

                event.getPlayer().sendMessage(
                        LoginToFiles.Messages.getMessage(MessageKeys.REGISTER_PROMPT_CHARACTERS.path(), event.getPlayer(), plugin)
                                .replace(
                                        "%characters%",
                                        LoginToFiles.Config.getString(ConfigKeys.PASSWORD_REQUIREMENTS_REQUIRED_CHAR_LIST.path(), plugin)
                                ) +
                                ChatColor.GRAY +
                                watermark
                );

            } else {
                event.getPlayer().sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.REGISTER_PROMPT.path(), event.getPlayer(), plugin));
            }
        }

        public static void startCounter(PlayerJoinEvent event, Plugin plugin, boolean isLogged) {
            if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.AUTH_SECURITY_KICK_ON_AUTH_TIMEOUT.path(), plugin)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!Sessions.isPlayerLogged(event.getPlayer()) && !isLogged) {
                            event.getPlayer().kickPlayer(LoginToFiles.Messages.getMessage(MessageKeys.ERRORS_LOGIN_FAIL_ONKICK_LONG_WAITING.path(), event.getPlayer(), plugin));
                        }
                    }
                }.runTaskLater(plugin, LoginToFiles.Config.getInt(ConfigKeys.AUTH_SECURITY_AUTH_TIMEOUT_SECONDS.path(), plugin) * 20L);

            }
        }
    }
}

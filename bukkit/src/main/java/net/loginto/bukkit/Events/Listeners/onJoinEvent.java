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
import net.loginto.bukkit.Utils.Premium.PremiumUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

        CompletableFuture.supplyAsync(() -> {

            return !database.isPlayerPresentInDB(event.getPlayer().getName());

        }).thenAccept(fistTimeInTheServer -> {

            boolean isLogged = Sessions.Proxy.isPlayerLoggedN(event.getPlayer(), plugin);

            if (!Sessions.isPlayerLogged(event.getPlayer()) && isLogged) {
                PlayerStatus.setPlayerAsLogged(event.getPlayer(), plugin, false, true);
                return;
            }

            PlayerStatus.setPlayerAsNotLogged(event.getPlayer(), plugin);

            if (fistTimeInTheServer) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    JoinUtil.sendRegisterMessages(event, plugin, watermark);
                    JoinUtil.startCounter(event, plugin, isLogged);
                });
                return;
            }

            boolean canAutoLogin = PremiumUtils.PlayerPremium.CheckIfAPlayerCanAutoLogin(event.getPlayer(), plugin);

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (canAutoLogin || isLogged) {
                    event.getPlayer().sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.LOGIN_SUCCESS.path(), event.getPlayer(), plugin));
                    PlayerStatus.setPlayerAsLogged(event.getPlayer(), plugin, true, true);
                } else {
                    event.getPlayer().sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.LOGIN_PROMPT.path(), event.getPlayer(), plugin));
                    JoinUtil.startCounter(event, plugin, isLogged);
                    Tries.insertPlayerWithZeroTries(event.getPlayer());
                }
            });
        });
    }


    private static class JoinUtil {
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

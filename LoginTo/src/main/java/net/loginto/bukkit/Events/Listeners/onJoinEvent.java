/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Events.Listeners;

import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.loginto.bukkit.PlayerUtils.PlayerStatus;
import net.loginto.bukkit.PlayerUtils.Sessions;
import net.loginto.bukkit.PlayerUtils.Tries;
import net.loginto.bukkit.Storage.Database;
import net.loginto.bukkit.Utils.LoginToFiles;
import net.loginto.bukkit.Utils.Premium.PremiumUtils;

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

        String watermark = (LoginToFiles.Config.isFeatureEnabled("plugin-utility.show-watermark", plugin)) ? " - Service offered by LoginTo on Modrinth" : "";
        
        CompletableFuture.supplyAsync(() -> {

            return !database.isPlayerPresentInDB(event.getPlayer());
            
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
                    event.getPlayer().sendMessage(LoginToFiles.Messages.getMessage("login.login-success", event.getPlayer(), plugin));
                    PlayerStatus.setPlayerAsLogged(event.getPlayer(), plugin, true, true);
                } else {
                    event.getPlayer().sendMessage(LoginToFiles.Messages.getMessage("login.login-prompt", event.getPlayer(), plugin));
                    JoinUtil.startCounter(event, plugin, isLogged);
                    Tries.insertPlayerWithZeroTries(event.getPlayer());
                }
            });
        });
    }


    private static class JoinUtil {
        public static void sendRegisterMessages(PlayerJoinEvent event, Plugin plugin, String watermark) {
            if (LoginToFiles.Config.isFeatureEnabled("password-requirements.require-special-chars", plugin)) {
                
                event.getPlayer().sendMessage(
                    LoginToFiles.Messages.getMessage("register.register-prompt-characters", event.getPlayer(), plugin)
                    .replace(
                        "%characters%", 
                        LoginToFiles.Config.getString("password-requirements.required-char-list", plugin)
                    ) + 
                    ChatColor.GRAY +  
                    watermark
                );

            } else {
                event.getPlayer().sendMessage(LoginToFiles.Messages.getMessage("register.register-prompt", event.getPlayer(), plugin));
            }
        }

        public static void startCounter(PlayerJoinEvent event, Plugin plugin, boolean isLogged) {
            if (LoginToFiles.Config.isFeatureEnabled("auth-security.kick-on-auth-timeout", plugin)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!Sessions.isPlayerLogged(event.getPlayer()) && !isLogged) {
                            event.getPlayer().kickPlayer(LoginToFiles.Messages.getMessage("errors.login-fail.onkick-for-long-waiting", event.getPlayer(), plugin));
                        }
                    }
                }.runTaskLater(plugin,LoginToFiles.Config.getInt("auth-security.auth-timeout-seconds", plugin) * 20L);
                
            }
        }
    }
}

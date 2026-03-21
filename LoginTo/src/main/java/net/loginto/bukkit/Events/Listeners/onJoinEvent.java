/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Events.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.loginto.bukkit.PlayerUtils.PlayerStatus;
import net.loginto.bukkit.PlayerUtils.Positions;
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

        Boolean fistTimeInTheServer = !database.isPlayerPresentInDB(event.getPlayer());
        String watermark = ((Boolean) LoginToFiles.Config.get("plugin-utility.show-watermark", plugin)) ? " - Service offered by LoginTo on Modrinth" : "";
        
        if (Sessions.Proxy.isPlayerLoggedN(event.getPlayer(), plugin) && !Sessions.isPlayerLogged(event.getPlayer())) {
            PlayerStatus.setPlayerAsLogged(event.getPlayer(), plugin, false);
            return;
        }

        PlayerStatus.setPlayerAsNotLogged(event.getPlayer());

        if (fistTimeInTheServer) {
            JoinUtil.sendRegisterMessages(event, plugin, watermark);
            JoinUtil.startCounter(event, plugin);
            return;
        }

        if ((Boolean) LoginToFiles.Config.get("premium.enable-premium-features", plugin)) {
            if (
                PremiumUtils.PlayerPremium.CheckIfAPlayerCanAutoLogin(event.getPlayer(), plugin) ||
                Sessions.Proxy.isPlayerLoggedN(event.getPlayer(), plugin)
            ) {

                event.getPlayer().sendMessage(LoginToFiles.Messages.getMessage("login.login-success", event.getPlayer(), plugin));

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Positions.teleportPlayerToTheOldPos(event.getPlayer(), plugin);
                }, 20);
                
                PlayerStatus.setPlayerAsLogged(event.getPlayer(), plugin, true);
                return;

            }
        }

        event.getPlayer().sendMessage(LoginToFiles.Messages.getMessage("login.login-prompt", event.getPlayer(), plugin));
        Tries.insertPlayerWithZeroTries(event.getPlayer());
    }


    private static class JoinUtil {
        public static void sendRegisterMessages(PlayerJoinEvent event, Plugin plugin, String watermark) {
            if ((Boolean) LoginToFiles.Config.get("password-requirements.require-special-chars", plugin)) {
                
                event.getPlayer().sendMessage(
                    LoginToFiles.Messages.getMessage("register.register-prompt-characters", event.getPlayer(), plugin)
                    .replace(
                        "%characters%", 
                        (String) LoginToFiles.Config.get("password-requirements.required-char-list", plugin)
                    ) + 
                    ChatColor.GRAY +  
                    watermark
                );

            } else {
                event.getPlayer().sendMessage(LoginToFiles.Messages.getMessage("register.register-prompt", event.getPlayer(), plugin));
            }
        }

        public static void startCounter(PlayerJoinEvent event, Plugin plugin) {
            if ((Boolean) LoginToFiles.Config.get("auth-security.kick-on-auth-timeout", plugin)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!Sessions.isPlayerLogged(event.getPlayer()) && !Sessions.Proxy.isPlayerLoggedN(event.getPlayer(), plugin)) {
                            event.getPlayer().kickPlayer(LoginToFiles.Messages.getMessage("errors.login-fail.onkick-for-long-waiting", event.getPlayer(), plugin));
                        }
                    }
                }.runTaskLater(plugin,(int) LoginToFiles.Config.get("auth-security.auth-timeout-seconds", plugin) * 20L);
            }
        }
    }
}

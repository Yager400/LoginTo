/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Commands;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import net.loginto.bukkit.LoginTo;
import net.loginto.bukkit.PlayerUtils.PasswordSecurity;
import net.loginto.bukkit.Storage.Database;
import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import net.loginto.bukkit.Utils.Files.MessageKeys;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ChangePassword implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private final Database database;

    public ChangePassword(Plugin plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Not a player");
            return false;
        }

        Player player = (Player) sender;


        if (!sender.hasPermission("loginto.changepassword")) {
            player.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.ERRORS_GENERAL_NO_PERMISSION.path(), player, plugin));
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.CHANGEPASSWORD_ERROR_USAGE.path(), player, plugin));
            return true;
        }

        String newPassword = args[0];
        int otpCode;
        try {
            otpCode = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid otp code");
            return false;
        }

        if (!PasswordSecurity.doesIncludeReqChars(newPassword, plugin)) {
            sender.sendMessage(
                    LoginToFiles.Messages.getMessage(MessageKeys.REGISTER_ERROR_CHARACTER_ERROR.path(), player, plugin)
                            .replace(
                                    "%characters%",
                                    LoginToFiles.Config.getString(ConfigKeys.PASSWORD_REQUIREMENTS_REQUIRED_CHAR_LIST.path(), plugin)
                            ));
            return true;
        }

        if (!PasswordSecurity.matchesLengthRequirement(newPassword, plugin, player)) {
            // The message get send in the PasswordSecurity.doesMatchLength function
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (PasswordSecurity.isCommon(newPassword, plugin, player.getName())) {
                player.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.REGISTER_ERROR_PASSWORD_TOO_SIMPLE.path(), player, plugin));
                return;
            }

            //OTP Code check
            try {

                GoogleAuthenticator gAuth = new GoogleAuthenticator();
                String secret = database.getSecret(player.getName());

                if (secret == null) {
                    player.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.CHANGEPASSWORD_ERROR_NO_OTP_CODE.path(), player, plugin));
                    return;
                }

                if (gAuth.authorize(secret, otpCode)) {
                    database.changePlayerPassword(player.getName(), newPassword);
                    player.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.CHANGEPASSWORD_PASSWORD_CHANGED.path(), player, plugin));
                } else {
                    player.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.CHANGEPASSWORD_ERROR_OTP_CODE_WRONG.path(), player, plugin));
                }
            } catch (Exception e) {
                plugin.getLogger().severe(e.getMessage());
            }
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            list.add("<newPassword>");
            return list;
        }
        if (args.length == 2) {
            List<String> list = new ArrayList<>();
            list.add("<otp_code>");
            return list;
        }

        return null;
    }
}

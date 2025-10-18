package net.loginto.Commands;

import static net.loginto.Configuration.Config.isFeatureEnabled;
import static net.loginto.Configuration.Messages.getMessage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;




import net.loginto.JSON.JsonMenager;

public class ChangePassword implements CommandExecutor  {



    private final JavaPlugin plugin;

    public ChangePassword(JavaPlugin plugin) {
        this.plugin = plugin;
    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("loginto.changepassword")) {
            sender.sendMessage(getMessage("errors.no_permission", plugin));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(getMessage("changepassword.correct_use_of_changepassword", plugin));
            return true;
        }

        String old_password = args[0];
        String new_password = args[1];

        JsonMenager file = new JsonMenager(plugin.getDataFolder(), "data.json");

        if (!old_password.equals(file.getString(sender.getName() + ".password"))) {
            sender.sendMessage(getMessage("changepassword.old_password_wrong", plugin));
            return true;
        }

        file.set(sender.getName() + ".password", new_password);
        file.save();

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (isFeatureEnabled("kick-rules.kick_on_password_change", plugin)) {
                player.kickPlayer(getMessage("changepassword.change_psw_success_disconnected", plugin));
            } else {
                sender.sendMessage(getMessage("changepassword.change_psw_success", plugin));
            }
        }


        return true;
    }
}

package net.loginto.ExtraFeature;

import static net.loginto.Configuration.Config.getStringFromConfig;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class VelocityServer {

    public static void sendPlayerToServer(Player player, Plugin plugin) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            out.writeUTF("Connect");
            out.writeUTF(getStringFromConfig("proxy-integration.server_name", plugin));

            player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

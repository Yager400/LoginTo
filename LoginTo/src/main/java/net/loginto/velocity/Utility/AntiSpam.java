/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.velocity.Utility;

import static net.loginto.velocity.Utility.FileMGR.YamlRead;

import java.io.File;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

import net.loginto.velocity.LoginTo;
import com.velocitypowered.api.proxy.ProxyServer;

public class AntiSpam {

    private final ProxyServer server;
    private final LoginTo plugin;

    public AntiSpam(ProxyServer server, LoginTo plugin) {
        this.server = server;
        this.plugin = plugin;
    }

    public static String sha256(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes("UTF-8"));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }
    
   public boolean isIpOver(String ip) {

        if (!Boolean.parseBoolean(YamlRead("anti_join-spam.anti_join-spam"))) {
            return false;
        }

        try {
            String hashed = sha256(ip);
            File folder = new File("plugins/loginto");

            JsonMenager json = new JsonMenager(folder, "antispam.json");

            if (json.getInt(hashed + ".connection") <= 0) {
                server.getScheduler().buildTask(plugin, () -> {
                    JsonMenager jsonTask = new JsonMenager(folder, "antispam.json");
                    if (!jsonTask.getBoolean(hashed + ".blocked")) {
                        jsonTask.remove(hashed);
                        jsonTask.save();
                    }
                    
                }).delay(Integer.parseInt(YamlRead("anti_join-spam.cooldown")), TimeUnit.SECONDS).schedule();
                return false;
            }

            if (json.getBoolean(hashed + ".blocked")) {
                return true;
            }

            

            int connections = json.getInt(hashed + ".connection");
            int max = Integer.parseInt(YamlRead("anti_join-spam.max-connection"));

            if (connections >= max) {
                json.set(hashed + ".blocked", true);
                json.save();

                server.getScheduler().buildTask(plugin, () -> {
                    JsonMenager jsonTask = new JsonMenager(folder, "antispam.json");
                    if (jsonTask.getBoolean(hashed + ".blocked")) {
                        jsonTask.remove(hashed);
                    }
                    jsonTask.save();
                }).delay(Integer.parseInt(YamlRead("anti_join-spam.ban-time")), TimeUnit.SECONDS).schedule();

                return true;
            }

            

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void incrementConnection(String ip) {
        if (!Boolean.parseBoolean(YamlRead("anti_join-spam.anti_join-spam"))) {
            return;
        }

        try {
            String hashed = sha256(ip);

            JsonMenager json = new JsonMenager(new File("plugins/loginto"), "antispam.json");

            json.set(hashed + ".connection", json.getInt(hashed + ".connection") + 1);
            if (json.getInt(hashed + ".connection") <= Integer.parseInt(YamlRead("anti_join-spam.max-connection"))) {
                json.set(hashed + ".blocked", false);
            } else {
                json.set(hashed + ".blocked", true);
            }
            json.save();
        } catch (Exception e) {} 
    }
}

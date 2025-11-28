package net.loginto.ExtraFeature;

import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Utility {
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

    public static void checkForUpdates(Plugin plugin) {
        try {

            URL url = new URL("https://raw.githubusercontent.com/Yager400/LoginTo/main/LoginTo/src/main/resources/plugin.yml");
            
            InputStreamReader reader = new InputStreamReader(url.openStream());
            YamlConfiguration gitConfig = YamlConfiguration.loadConfiguration(reader);

            String gitVersion = gitConfig.getString("version");
            String currentVersion = plugin.getDescription().getVersion();

            if (gitVersion == null) return;

            if (!currentVersion.equals(gitVersion)) {
                plugin.getLogger().info("A new update for LoginTo is out! Check it on https://modrinth.com/plugin/loginto");
            }

        } catch (Exception e) {
            
        }
    }
}

/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils;

import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import org.bukkit.plugin.Plugin;

public class TemporaryPremiumFeatureConfig {

    public static void setPremiumFeature(Plugin plugin, String fileVersion) {
        String ver = LoginToFiles.Config.getString(ConfigKeys.CONFIG_VERSION.path(), plugin);

        if (ver.equalsIgnoreCase(fileVersion)) {
            return;
        }

        if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PREMIUM_ENABLE_PREMIUM_FEATURES.path(), plugin)) {
            plugin.getLogger().severe("\n*****\n\n\"Premium authentication via both Bukkit and Proxy has been discontinued. \nPlease remove the plugin from your Bukkit servers and install version 3.7.0 exclusively on the proxy.\n\n*****");
            LoginToFiles.Config.setConfigValue(ConfigKeys.PREMIUM_ENABLE_PREMIUM_FEATURES.path(), false, plugin);
            return;
        }

        if (!LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PLUGIN_UTILITY_USE_EXPERIMENTAL_FEATURES.path(), plugin)) {
            return;
        }

        if (!LoginToFiles.Experimental.getExperimentalBoolean("premium.bukkit-premium-auth", plugin)) {
            return;
        }

        plugin.getLogger().info("The bukkit premium authentication is now stable and has been automatically moved to the config.");
        LoginToFiles.Config.setConfigValue(ConfigKeys.PREMIUM_ENABLE_PREMIUM_FEATURES.path(), true, plugin);
        LoginToFiles.Experimental.setExperimentalValue("premium.bukkit-premium-auth", false, plugin);
    }
}

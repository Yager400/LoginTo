/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.utils;

import com.github.yager400.minecraftupdatechecker.MinecraftUpdateChecker;
import com.github.yager400.minecraftupdatechecker.UpdateData;
import com.github.yager400.minecraftupdatechecker.data.Loader;
import com.github.yager400.minecraftupdatechecker.data.MinecraftVersion;
import com.github.yager400.minecraftupdatechecker.data.PluginProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.function.Consumer;

public class Updates {

    public static void checkForUpdates(String currentVersion, Consumer<UpdateData> onUpdate, Runnable errorUpdate) {
        new MinecraftUpdateChecker()
                .setPluginIdentifier("login-to")
                .setVersion(currentVersion)
                .setLoaders(Loader.VELOCITY, Loader.BUNGEECORD, Loader.WATERFALL, Loader.BUKKIT, Loader.SPIGOT, Loader.PAPER, Loader.PURPUR, Loader.FOLIA)
                .setMinecraftVersionRange(MinecraftVersion.V1_13, MinecraftVersion.V26_3)
                .setPluginProvider(PluginProvider.MODRINTH)
                .onUpdatePresent(onUpdate)
                .onError(scheduleError -> {
                    errorUpdate.run();
                })
                .start();
    }
}

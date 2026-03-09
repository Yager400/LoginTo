/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */

package net.loginto.bukkit;

import net.loginto.bukkit.Commands.*;
import net.loginto.bukkit.Configuration.YMLVersion;
import net.loginto.bukkit.DataBases.DataBase;

import static net.loginto.bukkit.ExtraFeature.Utility.checkForUpdates;
import static net.loginto.bukkit.Configuration.Config.*;
//import static net.loginto.bukkit.Configuration.Version.checkFilesVersion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import net.loginto.bukkit.ExtraFeature.Metrics;
import net.loginto.bukkit.ExtraFeature.Metrics.SimplePie;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.retrooper.packetevents.PacketEvents;

import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;

public class Main extends JavaPlugin {

    private DataBase database;

    @Override
    public void onLoad() {

        LibraryDownloader.Libs(this);

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();

    }

    @Override
    public void onEnable() {

        getLogger().warning("LoginTo started");


        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("PlaceHolderAPI not found, the plugin will use only the built-in placeholders");
        }


        PacketEvents.getAPI().init();

        Metrics Metrics = new Metrics(this, 28083);

        
        database = null;

        String storageType = getStringFromConfig("data.data-saving-type", this);
        if (
            storageType == null ||
            !storageType.equals("json") &&
            !storageType.equals("sqlite") &&
            !storageType.equals("mysql") &&
            !storageType.equals("postgre") &&
            !storageType.equals("h2")
        ) {
            getLogger().severe("data-saving-type is invalid, using json");
            Metrics.addCustomChart(new SimplePie("storage_type_used", () -> {
                    return "json";
            }));
        } else {
           if (!storageType.equals("json")) {
                database = new DataBase(this, storageType);
           }

            if (!storageType.equals("") || storageType != null) {
                Metrics.addCustomChart(new SimplePie("storage_type_used", () -> {
                        return storageType.toLowerCase();
                }));
            }
           
        }
        
        if (isFeatureEnabled("utility.update-checker", this)) checkForUpdates(this);
        
        

        

        this.getCommand("login").setExecutor(new Login(this, database));
        this.getCommand("register").setExecutor(new Register(this, database));
        this.getCommand("delacc").setExecutor(new DelAcc(this, database));
        this.getCommand("changepassword").setExecutor(new ChangePassword(this, database));
        this.getCommand("premium").setExecutor(new Premium(this));
        this.getCommand("cracked").setExecutor(new Cracked(this));
        this.getCommand("getlogs").setExecutor(new PlayersLog(this));

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "loginto:authchannel");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");


        Listeners.implementaListeners(this, database);

        createBasicFile(this);

        //checkFilesVersion(this);
        try {
            YMLVersion.builder()
                .plugin(this)
                .version("1.6")
                .resource("config.yml")
                .versionKey("ConfigVersion")
                .build();
            YMLVersion.builder()
                .plugin(this)
                .version("1.4")
                .resource("messages.yml")
                .versionKey("MessageVersion")
                .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        getLogger().warning("LoginTo ended loading");
    }

    public static void createBasicFile(Plugin plugin) {

        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        File messageFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messageFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        File oldPosFile = new File(plugin.getDataFolder(), "oldPosition.json");
        if (!oldPosFile.exists()) {
            plugin.saveResource("oldPosition.json", false);
        }
        
        File dataFile = new File(plugin.getDataFolder(), "data.json");
        if (!dataFile.exists()) {
            try {
                if (!plugin.getDataFolder().exists()) {
                    plugin.getDataFolder().mkdirs();
                }

                dataFile.createNewFile();
                Files.write(dataFile.toPath(), "{}".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        if (database != null) {
            database.close();
        }
        PacketEvents.getAPI().terminate();
    }

}

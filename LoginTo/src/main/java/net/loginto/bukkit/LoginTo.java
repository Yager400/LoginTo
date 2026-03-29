/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.retrooper.packetevents.PacketEvents;

import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.loginto.bukkit.Commands.*;
import net.loginto.bukkit.Events.Listener;
import net.loginto.bukkit.Events.Listeners.CancelledEvents;
import net.loginto.bukkit.Events.Listeners.logAnotherLocEvents;
import net.loginto.bukkit.Events.Listeners.onJoinEvent;
import net.loginto.bukkit.Events.Listeners.onPreCommandProcessEvent;
import net.loginto.bukkit.Events.Listeners.onQuitEvent;
import net.loginto.bukkit.Storage.Database;
import net.loginto.bukkit.Storage.Databases.H2;
import net.loginto.bukkit.Storage.Databases.MySQL;
import net.loginto.bukkit.Storage.Databases.PostgreSQL;
import net.loginto.bukkit.Storage.Databases.SQLite;
import net.loginto.bukkit.Utils.LibraryDownloader;
import net.loginto.bukkit.Utils.LoginToFiles;
import net.loginto.bukkit.Utils.Metrics;
import net.loginto.bukkit.Utils.Update;
import net.loginto.bukkit.Utils.Metrics.SimplePie;
import net.loginto.bukkit.Utils.Premium.PremiumUtils;
import net.loginto.bukkit.Utils.YMLVersion;

public class LoginTo extends JavaPlugin {

    private Database database;

    @Override
    public void onLoad() {
        LibraryDownloader.Libs(this);

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }
    
    @Override
    public void onEnable() {

        getLogger().warning("LoginTo started loading...");

        PacketEvents.getAPI().init();

        //Metrics
        Metrics metrics = new Metrics(this, 28083);
        //----
        
        

        //Saving default files
        LoginToFiles.saveFiles(this);
        //-----

        //Updating yaml files
        //TODO
        try {
            YMLVersion.builder()
                .plugin(this)
                .version("1.8")
                .resource("config.yml")
                .versionKey("ConfigVersion")
                .build();
            YMLVersion.builder()
                .plugin(this)
                .version("1.6")
                .resource("messages.yml")
                .versionKey("MessageVersion")
                .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //-----

        //Initializing databases
        String databaseType = LoginToFiles.Config.getString("storage.storage-type", this);
        database = null;
        switch (databaseType) {
            case "sqlite": 
                database = new SQLite(this);
                metrics.addCustomChart(new SimplePie("storage_type_used", () -> {
                    return "sqlite";
                }));
                break;
            case "mysql": 
                database = new MySQL(this);
                metrics.addCustomChart(new SimplePie("storage_type_used", () -> {
                    return "mysql";
                }));
                break;
            case "postgresql": 
                database = new PostgreSQL(this); 
                metrics.addCustomChart(new SimplePie("storage_type_used", () -> {
                    return "postgresql";
                }));
                break;
            case "h2": 
                database = new H2(this); 
                metrics.addCustomChart(new SimplePie("storage_type_used", () -> {
                    return "h2";
                }));
                break;
            default: 
                getLogger().severe("Database type '" + databaseType + "' is not valid, using sqlite");
                database = new SQLite(this);
                metrics.addCustomChart(new SimplePie("storage_type_used", () -> {
                    return "sqlite";
                }));
                break;
        }
        //Connecting premium database
        if (LoginToFiles.Config.isFeatureEnabled("premium.enable-premium-features", this)) {
            PremiumUtils.connectAndGetSource(this);
        }

        //-----

        //Registering listener
        getServer().getPluginManager().registerEvents(new CancelledEvents(this), this);

        getServer().getPluginManager().registerEvents(new onJoinEvent(this, database), this);
        getServer().getPluginManager().registerEvents(new onPreCommandProcessEvent(this), this);
        getServer().getPluginManager().registerEvents(new onQuitEvent(this), this);
        getServer().getPluginManager().registerEvents(new logAnotherLocEvents(this), this);

        Listener.implementPacketEventListener();
        //-----

        //Add commands
        getCommand("register").setExecutor(new Register(this, database));
        getCommand("register").setTabCompleter(new Register(this, database));
        
        getCommand("login").setExecutor(new Login(this, database));
        getCommand("login").setTabCompleter(new Login(this, database));
        
        getCommand("delacc").setExecutor(new DelAcc(this, database));
        getCommand("delacc").setTabCompleter(new DelAcc(this, database));
        
        getCommand("changepassword").setExecutor(new ChangePassword(this, database));
        getCommand("changepassword").setTabCompleter(new ChangePassword(this, database));
        
        getCommand("premium").setExecutor(new Premium(this, database));
        getCommand("premium").setTabCompleter(new Premium(this, database));
        
        getCommand("cracked").setExecutor(new Cracked(this, database));

        getCommand("getlogs").setExecutor(new getlogs(this));
        getCommand("getlogs").setTabCompleter(new getlogs(this));
        //-----

        //Register channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        //------

        //Updated
        Update.checkForUpdates(this);
        //-----

        getLogger().warning("LoginTo loaded!");

    }

    @Override
    public void onDisable() {
        database.close();
        PacketEvents.getAPI().terminate();
    }
}

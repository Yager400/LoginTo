package net.loginto;

import static net.loginto.Listeners.implementaListeners;
import net.loginto.Commands.*;
import net.loginto.DataBases.DataBase;

import static net.loginto.Configuration.ConfigMenager.VersionChecker.checkFilesVersion;
import static net.loginto.ExtraFeature.Utility.checkForUpdates;
import static net.loginto.Configuration.Config.*;




import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.loginto.ExtraFeature.BStats;
import net.loginto.ExtraFeature.BStats.SimplePie;

public class Main extends JavaPlugin {

    private DataBase database;

    @Override
    public void onEnable() {
        getLogger().warning("LoginTo started");

        BStats Metrics = new BStats(this, 28083);

        
        database = null;

        String storageType = getStringFromConfig("data.data-saving-type", this);
        if (
            storageType == null ||
            !storageType.equals("json") &&
            !storageType.equals("sqlite") &&
            !storageType.equals("mysql") &&
            !storageType.equals("postgre")
        ) {
            getLogger().severe("data-saving-type is invalid, using json");
        } else {
           if (!storageType.equals("json")) {
                database = new DataBase(this, storageType);
           }

            if (storageType.equals("") || storageType == null) {
                Metrics.addCustomChart(new SimplePie("storage_type_used", () -> {
                        return storageType;
                }));
            }
           
        }
        
        if (isFeatureEnabled("utility.update-checker", this)) checkForUpdates(this);
        

        

        this.getCommand("login").setExecutor(new Login(this, database));
        this.getCommand("register").setExecutor(new Register(this, database));
        this.getCommand("delacc").setExecutor(new DelAcc(this, database));
        this.getCommand("changepassword").setExecutor(new ChangePassword(this, database));



        implementaListeners(this, database);

        createBasicFile(this);

        checkFilesVersion(this);
        
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
    }

}


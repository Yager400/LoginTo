package net.loginto;

import static net.loginto.Listeners.implementaListeners;
import net.loginto.Commands.*;
import net.loginto.DataBases.DataBase;

import static net.loginto.Configuration.ConfigMenager.BasicFileContent.Config.getDefaultConfigFileContent;
import static net.loginto.Configuration.ConfigMenager.BasicFileContent.Messages.getDefaultMessageFileContent;
import static net.loginto.Configuration.ConfigMenager.VersionChecker.checkFilesVersion;




import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.loginto.ExtraFeature.BStats;

import static net.loginto.Configuration.Config.*;

/* 
import net.loginto.DataBases.MySQL.MySQL;
import net.loginto.DataBases.PostgreSQL.PostgreSQL;
import net.loginto.DataBases.SQLite.SQLite;
*/

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
        }
        
         
        

        

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
            try { Files.write(configFile.toPath(), getDefaultConfigFileContent().getBytes()); } catch (IOException ignored) {}
        }


        File messageFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messageFile.exists()) {
            try { Files.write(messageFile.toPath(), getDefaultMessageFileContent().getBytes()); } catch (IOException ignored) {}
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
        database.close();
    }

}

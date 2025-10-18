package net.loginto;

import static net.loginto.Listeners.implementaListeners;
import net.loginto.Commands.*;
import static net.loginto.Configuration.ConfigMenager.BasicFileContent.Config.getDefaultConfigFileContent;
import static net.loginto.Configuration.ConfigMenager.BasicFileContent.Messages.getDefaultMessageFileContent;
import static net.loginto.Configuration.ConfigMenager.VersionChecker.checkFilesVersion;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().warning("LoginTo started");

        implementaListeners(this);

        this.getCommand("login").setExecutor(new Login(this));
        this.getCommand("register").setExecutor(new Register(this));
        this.getCommand("delacc").setExecutor(new DelAcc(this));
        this.getCommand("changepassword").setExecutor(new ChangePassword(this));



        

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




    //TODO
    /*
     * 
     * Vedere se tutto funziona, possibilmente anche testarlo con un giocatore a parte
     * 
     * 
     */


}

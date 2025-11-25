package net.loginto.DataBases;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class DataBase {


    private String storage;

    private final Plugin plugin;

    private MySQL mysql;
    private PostgreSQL postgresql;
    private SQLite sqlite;
    

    public DataBase(Plugin givedPlugin, String storageType) {
        this.plugin = givedPlugin;

        this.storage = storageType;

        mysql = new MySQL(plugin);
        postgresql = new PostgreSQL(plugin);
        sqlite = new SQLite(plugin);

        try {
            switch (storageType) {
                case "mysql" -> mysql.connectMySQL();
                case "postgre" -> postgresql.connectPostgreSQL();
                case "sqlite" -> sqlite.connectSQLite();
            }
        } catch (Exception e) {}
    }


    public void close() {
        switch (storage) {
            case "mysql" -> mysql.close();
            case "postgre" -> postgresql.close();
            case "sqlite" -> sqlite.close();
        }
    }











    
    public boolean isPlayerPresentInDB(Player player) {
        boolean result = false;
        switch (storage) {
            case "mysql" -> result = mysql.isPlayerPresentInDB(player);
            case "postgre" -> result =  postgresql.isPlayerPresentInDB(player);
            case "sqlite" -> result = sqlite.isPlayerPresentInDB(player);
        }
        return result;
    }

    public boolean isPasswordCorrect(Player player, String password) throws Exception {
        boolean result = false;
        switch (storage) {
            case "mysql" -> result = mysql.isPasswordCorrect(player, password);
            case "postgre" -> result =  postgresql.isPasswordCorrect(player, password);
            case "sqlite" -> result = sqlite.isPasswordCorrect(player, password);
        }
        return result;
    }

    public boolean insertPlayer(Player player, String password) throws Exception {
        boolean result = false;
        switch (storage) {
            case "mysql" -> result = mysql.insertPlayer(player, password);
            case "postgre" -> result =  postgresql.insertPlayer(player, password);
            case "sqlite" -> result = sqlite.insertPlayer(player, password);
        }
        return result;



    }

    public String changePlayerPassword(Player player, String oldPassword, String newPassword) throws Exception {
        String result = "DBERR1";
        switch (storage) {
            case "mysql" -> result = mysql.changePlayerPassword(player, oldPassword, newPassword);
            case "postgre" -> result =  postgresql.changePlayerPassword(player, oldPassword, newPassword);
            case "sqlite" -> result = sqlite.changePlayerPassword(player, oldPassword, newPassword);
        }
        return result;
    }

    public boolean removePlayer(Player player) {
        boolean result = false;
        switch (storage) {
            case "mysql" -> result = mysql.removePlayer(player);
            case "postgre" -> result =  postgresql.removePlayer(player);
            case "sqlite" -> result = sqlite.removePlayer(player);
        }
        return result;
    }

     
}

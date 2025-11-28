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
                case "mysql":
                    mysql.connectMySQL();
                    break;
                case "postgre":
                    postgresql.connectPostgreSQL();
                    break;
                case "sqlite":
                    sqlite.connectSQLite();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {}
    }


    public void close() {
        switch (storage) {
            case "mysql":
                mysql.close();
                break;
            case "postgre":
                postgresql.close();
                break;
            case "sqlite":
                sqlite.close();
                break;
            default:
                break;
        }
    }











    
    public boolean isPlayerPresentInDB(Player player) {
        boolean result = false;
        switch (storage) {
            case "mysql":
                result = mysql.isPlayerPresentInDB(player);
                break;
            case "postgre":
                result = postgresql.isPlayerPresentInDB(player);
                break;
            case "sqlite":
                result = sqlite.isPlayerPresentInDB(player);
                break;
            default:
                break;
        }
        return result;
    }

    public boolean isPasswordCorrect(Player player, String password) throws Exception {
        boolean result = false;
        switch (storage) {
            case "mysql":
                result = mysql.isPasswordCorrect(player, password);
                break;
            case "postgre":
                result = postgresql.isPasswordCorrect(player, password);
                break;
            case "sqlite":
                result = sqlite.isPasswordCorrect(player, password);
                break;
            default:
                break;
        }
        return result;
    }

    public boolean insertPlayer(Player player, String password) throws Exception {
        boolean result = false;
        switch (storage) {
            case "mysql":
                result = mysql.insertPlayer(player, password);
                break;
            case "postgre":
                result = postgresql.insertPlayer(player, password);
                break;
            case "sqlite":
                result = sqlite.insertPlayer(player, password);
                break;
            default:
                break;
        }
        return result;
    }

    public String changePlayerPassword(Player player, String oldPassword, String newPassword) throws Exception {
        String result = "DBERR1";
        switch (storage) {
            case "mysql":
                result = mysql.changePlayerPassword(player, oldPassword, newPassword);
                break;
            case "postgre":
                result = postgresql.changePlayerPassword(player, oldPassword, newPassword);
                break;
            case "sqlite":
                result = sqlite.changePlayerPassword(player, oldPassword, newPassword);
                break;
            default:
                break;
        }
        return result;
    }

     public boolean removePlayer(Player player) {
        boolean result = false;
        switch (storage) {
            case "mysql":
                result = mysql.removePlayer(player);
                break;
            case "postgre":
                result = postgresql.removePlayer(player);
                break;
            case "sqlite":
                result = sqlite.removePlayer(player);
                break;
            default:
                break;
        }
        return result;
    }

     
}

package net.loginto.bukkit.Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import net.loginto.bukkit.Utils.JsonToSqlite;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionUtils {

    public static Database connectSQLite(Plugin plugin) {
        String dbName = LoginToFiles.Config.getStringOrDefault(ConfigKeys.STORAGE_DATABASE_NAME.path(), plugin, "LoginTo_DB");

        File dbFile = new File(plugin.getDataFolder(), dbName + ".db");
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath().replace("\\", "/");

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setPoolName("LoginTo-SQLite");
        cfg.setMaximumPoolSize(1);
        cfg.setMinimumIdle(1);
        cfg.setConnectionTestQuery("SELECT 1");
        cfg.addDataSourceProperty("journal_mode", "WAL");
        cfg.addDataSourceProperty("foreign_keys", "on");
        cfg.setAutoCommit(true);

        HikariDataSource dataSource = new HikariDataSource(cfg);

        if (new File(plugin.getDataFolder(), "data.json").exists()) {
            JsonToSqlite.migrate(plugin, dataSource);
        }

        return new Database(dataSource, plugin);
    }

    public static Database connectMySQL(Plugin plugin) {
        String host = LoginToFiles.Config.getStringOrDefault(ConfigKeys.STORAGE_DATABASE_HOST.path(), plugin, "127.0.0.1");
        int port = LoginToFiles.Config.getIntOrDefault(ConfigKeys.STORAGE_DATABASE_PORT.path(), plugin, 5432);
        String dbName = LoginToFiles.Config.getStringOrDefault(ConfigKeys.STORAGE_DATABASE_NAME.path(), plugin, "LoginTo_DB");

        String user = LoginToFiles.Config.getString(ConfigKeys.STORAGE_DATABASE_USER.path(), plugin);
        String password = LoginToFiles.Config.getString(ConfigKeys.STORAGE_DATABASE_PASSWORD.path(), plugin);

        String urlNoDB = "jdbc:mysql://" + host + ":" + port + "/";
        String url = urlNoDB + dbName;

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername(user);
        cfg.setPassword(password);
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(2);
        cfg.setPoolName("LoginTo-MySQL");
        cfg.setConnectionTestQuery("SELECT 1");
        cfg.addDataSourceProperty("cachePrepStmts", "true");
        cfg.addDataSourceProperty("prepStmtCacheSize", "250");
        cfg.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        cfg.setDriverClassName("net.loginto.libs.mysql.cj.jdbc.Driver");

        HikariDataSource dataSource = new HikariDataSource(cfg);

        return new Database(dataSource, plugin);
    }

    public static Database connectPostgreSQL(Plugin plugin) {
        String host = LoginToFiles.Config.getStringOrDefault(ConfigKeys.STORAGE_DATABASE_HOST.path(), plugin, "127.0.0.1");
        int port = LoginToFiles.Config.getIntOrDefault(ConfigKeys.STORAGE_DATABASE_PORT.path(), plugin, 5432);
        String dbName = LoginToFiles.Config.getStringOrDefault(ConfigKeys.STORAGE_DATABASE_NAME.path(), plugin, "LoginTo_DB");

        String user = LoginToFiles.Config.getString(ConfigKeys.STORAGE_DATABASE_USER.path(), plugin);
        String password = LoginToFiles.Config.getString(ConfigKeys.STORAGE_DATABASE_PASSWORD.path(), plugin);

        String urlNoDB = "jdbc:postgresql://" + host + ":" + port + "/";
        String url = urlNoDB + dbName;

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername(user);
        cfg.setPassword(password);
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(2);
        cfg.setPoolName("LoginTo-PostgreSQL");
        cfg.setConnectionTestQuery("SELECT 1");
        cfg.addDataSourceProperty("cachePrepStmts", "true");
        cfg.addDataSourceProperty("prepStmtCacheSize", "250");
        cfg.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        cfg.setDriverClassName("net.loginto.libs.postgresql.Driver");

        HikariDataSource dataSource = new HikariDataSource(cfg);

        return new Database(dataSource, plugin);
    }

    public static Database connectH2(Plugin plugin) {
        String dbName = LoginToFiles.Config.getStringOrDefault(ConfigKeys.STORAGE_DATABASE_NAME.path(), plugin, "LoginTo_DB");

        File dbFile = new File(plugin.getDataFolder(), dbName);
        String url = "jdbc:h2:" + dbFile.getAbsolutePath().replace("\\", "/");

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername("sa");
        cfg.setPassword("");
        cfg.setPoolName("LoginTo-H2");
        cfg.setConnectionTestQuery("SELECT 1");
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(5);
        cfg.setAutoCommit(true);
        cfg.setDriverClassName("net.loginto.libs.h2.Driver");

        HikariDataSource dataSource = new HikariDataSource(cfg);

        return new Database(dataSource, plugin);
    }
}

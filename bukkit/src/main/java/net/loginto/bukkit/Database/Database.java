package net.loginto.bukkit.Database;

import com.zaxxer.hikari.HikariDataSource;
import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import net.loginto.bukkit.Utils.SecurityUtils;
import org.bukkit.plugin.Plugin;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class Database {

    private final Plugin plugin;
    private HikariDataSource dataSource;

    protected Database(HikariDataSource dataSource, Plugin plugin) {
        this.dataSource = dataSource;
        this.plugin = plugin;

        //-----
        String createTable = "create table if not exists LoginTo_Users(name text primary key not null, password text not null, secret text);";
        executePreparedQuery(createTable);

        if (!doesColumnExists("LoginTo_Users", "secret")) {
            String createSecretColumn = "alter table LoginTo_Users add column secret text;";

            executePreparedQuery(createSecretColumn);
        }

        String createPremiumTable = "create table if not exists LoginTo_PremiumAccStatus(name text, isPremium boolean);";
        executePreparedQuery(createPremiumTable);
    }


    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public HikariDataSource getSource() {
        return dataSource;
    }

    public void connect() {
        this.close();
        String databaseType = LoginToFiles.Config.getString(ConfigKeys.STORAGE_STORAGE_TYPE.path(), plugin);
        switch (databaseType) {
            case "sqlite": dataSource = DatabaseConnectionUtils.connectSQLite(plugin).getSource(); break;
            case "mysql": dataSource = DatabaseConnectionUtils.connectMySQL(plugin).getSource(); break;
            case "postgresql": dataSource = DatabaseConnectionUtils.connectPostgreSQL(plugin).getSource(); break;
            case "h2": dataSource = DatabaseConnectionUtils.connectH2(plugin).getSource(); break;
            default: dataSource = DatabaseConnectionUtils.connectSQLite(plugin).getSource(); break;
        }
    }

    public boolean ping() {
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(2);
        } catch (SQLException e) {
            plugin.getLogger().warning("Ping error: " + e.getMessage());
            return false;
        }
    }

    private boolean doesColumnExists(String tableName, String columnName) {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();

            try (ResultSet rs = meta.getColumns(null, null, tableName, columnName)) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void executePreparedQuery(String sql) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.executeUpdate();

        } catch (SQLException e) {
            plugin.getLogger().warning("Query error: " + e.getMessage());
        }
    }


    public boolean isPlayerPresentInDB(String playerName) {
        String sql = "select name from LoginTo_Users where name = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPasswordCorrect(String playerName, String password) throws Exception {
        String sql = "select name, password from LoginTo_Users where name = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (!rs.next()) {
                    return false;
                }

                String psw = rs.getString("password");

                try {
                    return BCrypt.checkpw(password, psw);
                } catch (IllegalArgumentException e) {
                    return psw.equals(SecurityUtils.sha256(password));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean insertPlayer(String playerName, String password) throws Exception {
        String hashedPassword = SecurityUtils.BCrypt(password);

        String sql = "insert into LoginTo_Users(name, password) values (?, ?) ;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);
            pstmt.setString(2, hashedPassword);

            pstmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;


    }

    public void changePlayerPassword(String playerName, String newPassword) throws Exception {
        String hashedNewPassword = SecurityUtils.BCrypt(newPassword);

        String sql = "update LoginTo_Users set password = ? where name = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hashedNewPassword);
            pstmt.setString(2, playerName);
            pstmt.execute();

        }
    }

    public boolean removePlayer(String playerName) {
        String sql = "delete from LoginTo_Users where name = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);
            pstmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            return false;
        }

    }

    public String getSecret(String playerName) {
        String sql = "select secret from LoginTo_Users where name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);
            ResultSet set = pstmt.executeQuery();

            if (set.next()) {
                return set.getString("secret");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setSecret(String playerName, String secret) {
        String sql = "update LoginTo_Users set secret = ? where name = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, secret);
            pstmt.setString(2, playerName);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPlayerAccountStatus(String playerName, boolean isPremium) {
        String sql = "insert into LoginTo_PremiumAccStatus(name, isPremium) values (?, ?);";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);
            pstmt.setBoolean(2, isPremium);

            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayerAccountStatus(String playerName, boolean isPremium) {
        String sql = "update LoginTo_PremiumAccStatus set isPremium = ? where name = ?;";

        if (!premiumTableContainsPlayer(playerName)) {
            insertPlayerAccountStatus(playerName, isPremium);
            return;
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, isPremium);
            pstmt.setString(2, playerName);

            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isPremium(String playerName) {
        String sql = "select isPremium from LoginTo_PremiumAccStatus where name = ?;";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);

            ResultSet set = pstmt.executeQuery();

            while (set.next()) {
                return set.getBoolean("isPremium");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean premiumTableContainsPlayer(String playerName) {
        String sql = "select 1 from LoginTo_PremiumAccStatus where name = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);

            ResultSet set = pstmt.executeQuery();

            return set.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void removePremiumPlayerRecords(String playerName) {
        String sql = "delete from LoginTo_PremiumAccStatus where name = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);

            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


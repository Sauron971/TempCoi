package org.kyas.tempcoi;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class TempCoi extends JavaPlugin {

    final String username = "admin";
    final String password = "admin";
    final String databaseName = "tempcoi.db";
    File pluginFolder;
    static Connection connection;
    String url = null;

    @Override
    public void onEnable() {
        pluginFolder = getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdir();
        }
        createDatabaseFile();
        createDB();

        getServer().getPluginManager().registerEvents(new ListenerChat(this), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new CheckTime(), 0L, 6000);
    }

    @Override
    public void onDisable() {
        try {
            if (connection!= null &&!connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDatabaseFile() {
        File dbFile = new File(pluginFolder, databaseName);
        try {
            if (!dbFile.exists()) {
                dbFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            url = "jdbc:sqlite:" + pluginFolder.getAbsolutePath() + "/" + databaseName;
            connection = DriverManager.getConnection(url, username, password);
            String sqlState = "CREATE TABLE IF NOT EXISTS players(name VARCHAR(15) PRIMARY KEY, lastUse DATE, timesUse INT);";
            PreparedStatement statement = connection.prepareStatement(sqlState);
            statement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
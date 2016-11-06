package net.rocketeer.sevens;

import net.rocketeer.sevens.database.DatabaseManager;
import net.rocketeer.sevens.database.SqlStreamExecutor;
import net.rocketeer.sevens.player.MySqlPlayerDatabase;
import net.rocketeer.sevens.player.PlayerDatabase;
import net.rocketeer.sevens.player.listener.DeathListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.sql.SQLException;

public class SevensPlugin extends JavaPlugin {
  private DatabaseManager databaseManager;
  private PlayerDatabase playerDatabase;

  public void initDatabase() throws PropertyVetoException {
    FileConfiguration config = this.getConfig();
    ConfigurationSection mysqlCfg = config.getConfigurationSection("mysql");
    String username = mysqlCfg.getString("username");
    String password = mysqlCfg.getString("password");
    String hostname = mysqlCfg.getString("hostname");
    String database = mysqlCfg.getString("database");
    int port = mysqlCfg.getInt("port");
    String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database;
    this.databaseManager =  new DatabaseManager(url, username, password);
    this.playerDatabase = new MySqlPlayerDatabase(this.databaseManager);
    InputStream stream = this.getClass().getResourceAsStream("/init.sql");
    try (SqlStreamExecutor executor = new SqlStreamExecutor(this.databaseManager.getConnection(), stream)) {
      executor.execute();
    } catch (SQLException exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void onEnable() {
    this.saveDefaultConfig();
    try {
      this.initDatabase();
    } catch (PropertyVetoException e) {
      e.printStackTrace();
      return;
    }
    Bukkit.getPluginManager().registerEvents(new DeathListener(this.playerDatabase), this);
  }

  @Override
  public void onDisable() {

  }
}

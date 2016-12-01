package net.rocketeer.sevens;

import net.rocketeer.sevens.command.HighScoreCommand;
import net.rocketeer.sevens.command.ScoreCommand;
import net.rocketeer.sevens.database.DatabaseManager;
import net.rocketeer.sevens.game.bounty.BountyNameTagListener;
import net.rocketeer.sevens.game.bounty.BountyRegistry;
import net.rocketeer.sevens.game.bounty.BountyRewardEvent;
import net.rocketeer.sevens.game.name.NameTagRegistry;
import net.rocketeer.sevens.game.name.StaticTagManager;
import net.rocketeer.sevens.game.spree.SpreeConfig;
import net.rocketeer.sevens.game.spree.SpreeListener;
import net.rocketeer.sevens.game.spree.SpreeRegistry;
import net.rocketeer.sevens.player.MySqlPlayerDatabase;
import net.rocketeer.sevens.player.PlayerDatabase;
import net.rocketeer.sevens.player.listener.BountyLoggingListener;
import net.rocketeer.sevens.player.listener.KillLoggingListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.beans.PropertyVetoException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SevensPlugin extends JavaPlugin {
  private DatabaseManager databaseManager;
  private PlayerDatabase playerDatabase;
  private NameTagRegistry registry;
  private StaticTagManager tagManager;

  public void initDatabase() throws PropertyVetoException {
    FileConfiguration config = this.getConfig();
    ConfigurationSection mysqlCfg = config.getConfigurationSection("mysql");
    String username = mysqlCfg.getString("username");
    String password = mysqlCfg.getString("password");
    String hostname = mysqlCfg.getString("hostname");
    String database = mysqlCfg.getString("database");
    int port = mysqlCfg.getInt("port");
    String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?autoReconnect=true";
    this.databaseManager =  new DatabaseManager(url, username, password);
    this.playerDatabase = new MySqlPlayerDatabase(this.databaseManager);
    this.databaseManager.initDatabase();
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
    List<String> worlds = this.getConfig().getStringList("worlds");
    Set<String> trackedWorlds = new HashSet<>();
    trackedWorlds.addAll(worlds);
    Bukkit.getPluginManager().registerEvents(new KillLoggingListener(this, this.playerDatabase, trackedWorlds), this);
    this.registry = new NameTagRegistry("name");
    registry.init(this);
    BountyRegistry bRegistry = new BountyRegistry("bounty");
    bRegistry.init(this);
    SpreeRegistry sRegistry = new SpreeRegistry("spree");
    sRegistry.init(this);
    // TODO refactor code
    SpreeConfig config = SpreeConfig.fromConfig(this.getConfig().getConfigurationSection("spree"));
    Bukkit.getPluginManager().registerEvents(new SpreeListener(config, bRegistry), this);
    Bukkit.getPluginManager().registerEvents(new BountyNameTagListener(this.registry), this);
    Bukkit.getPluginManager().registerEvents(new BountyLoggingListener(this, this.playerDatabase), this);
    Bukkit.getPluginCommand("scoretop").setExecutor(new HighScoreCommand(this, this.playerDatabase));
    Bukkit.getPluginCommand("score").setExecutor(new ScoreCommand(this, this.playerDatabase));
    this.tagManager = new StaticTagManager(this);
  }

  @Override
  public void onDisable() {
    this.tagManager.despawnAll();
  }
}

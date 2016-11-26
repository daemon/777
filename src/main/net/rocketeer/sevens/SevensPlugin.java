package net.rocketeer.sevens;

import net.rocketeer.sevens.database.DatabaseManager;
import net.rocketeer.sevens.game.bounty.BountyNameTagListener;
import net.rocketeer.sevens.game.bounty.BountyRegistry;
import net.rocketeer.sevens.game.name.NameTagRegistry;
import net.rocketeer.sevens.game.spree.SpreeConfig;
import net.rocketeer.sevens.game.spree.SpreeListener;
import net.rocketeer.sevens.game.spree.SpreeRegistry;
import net.rocketeer.sevens.player.MySqlPlayerDatabase;
import net.rocketeer.sevens.player.PlayerDatabase;
import net.rocketeer.sevens.player.listener.BountyLoggingListener;
import net.rocketeer.sevens.player.listener.KillLoggingListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.beans.PropertyVetoException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SevensPlugin extends JavaPlugin {
  private DatabaseManager databaseManager;
  private PlayerDatabase playerDatabase;
  private NameTagRegistry registry;

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
    SpreeConfig config = SpreeConfig.fromConfig(this.getConfig().getConfigurationSection("spree"));
    Bukkit.getPluginManager().registerEvents(new SpreeListener(config, bRegistry), this);
    Bukkit.getPluginManager().registerEvents(new BountyNameTagListener(this.registry), this);
    Bukkit.getPluginManager().registerEvents(new BountyLoggingListener(this, this.playerDatabase, bRegistry), this);
    Bukkit.getPluginCommand("ttt").setExecutor(new TestExecutor());
  }

  public class TestExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
      Player player = (Player) sender;
      if (strings.length == 0)
        registry.unregisterFakeName(player);
      String nn = strings[0];
      registry.registerNameTag(player, nn);
      return true;
    }
  }

  @Override
  public void onDisable() {

  }
}

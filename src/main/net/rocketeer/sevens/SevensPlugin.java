package net.rocketeer.sevens;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import net.rocketeer.sevens.stats.Property;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.sql.Connection;

public class SevensPlugin extends JavaPlugin {
  private SessionFactory factory;

  public void initDatabase() {
    FileConfiguration config = this.getConfig();
    ConfigurationSection mysqlCfg = config.getConfigurationSection("mysql");
    String username = mysqlCfg.getString("username");
    String password = mysqlCfg.getString("password");
    String hostname = mysqlCfg.getString("hostname");
    String database = mysqlCfg.getString("database");
    int port = mysqlCfg.getInt("port");
    String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database;
    Configuration hibernateCfg = new Configuration();
    hibernateCfg.configure();
    hibernateCfg.setProperty("hibernate.connection.url", url);
    hibernateCfg.setProperty("hibernate.connection.username", username);
    hibernateCfg.setProperty("hibernate.connection.password", password);
    hibernateCfg.setProperty("hibernate.hbm2ddl.auto", "create");
    hibernateCfg.addAnnotatedClass(Property.class);
    this.factory = hibernateCfg.buildSessionFactory();
  }

  @Override
  public void onEnable() {
    this.saveDefaultConfig();
    this.initDatabase();
  }

  @Override
  public void onDisable() {

  }
}

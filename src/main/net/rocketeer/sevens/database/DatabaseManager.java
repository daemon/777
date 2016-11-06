package net.rocketeer.sevens.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
  private final ComboPooledDataSource dataSource;
  public DatabaseManager(String url, String username, String password) throws PropertyVetoException {
    this.dataSource = new ComboPooledDataSource();
    this.dataSource.setDriverClass("com.mysql.jdbc.Driver");
    this.dataSource.setJdbcUrl(url);
    this.dataSource.setUser(username);
    this.dataSource.setPassword(password);
    this.dataSource.setMaxPoolSize(16);
    this.dataSource.setMinPoolSize(2);
  }

  public Connection getConnection() throws SQLException {
    return this.dataSource.getConnection();
  }
}

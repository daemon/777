package net.rocketeer.sevens.database;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionGuard implements AutoCloseable {
  private final Connection connection;

  public TransactionGuard(Connection connection) throws SQLException {
    connection.setAutoCommit(false);
    this.connection = connection;
  }

  @Override
  public void close() throws SQLException {
    try {
      this.connection.commit();
    } catch (SQLException e) {
      e.printStackTrace();
      this.connection.rollback();
    } finally {
      this.connection.setAutoCommit(true);
    }
  }
}

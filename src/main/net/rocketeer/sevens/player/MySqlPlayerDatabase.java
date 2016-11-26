package net.rocketeer.sevens.player;

import com.mysql.jdbc.Statement;
import net.rocketeer.sevens.database.DatabaseManager;
import net.rocketeer.sevens.database.TransactionGuard;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySqlPlayerDatabase implements PlayerDatabase {
  private final DatabaseManager manager;

  public MySqlPlayerDatabase(DatabaseManager manager) {
    this.manager = manager;
  }

  public SevensPlayer findPlayer(UUID uuid) throws SQLException, IOException {
    try (Connection c = this.manager.getConnection();
         TransactionGuard guard = new TransactionGuard(c)) {
      try (PreparedStatement stmt = c.prepareStatement("SELECT * FROM svns_players WHERE uuid=? FOR UPDATE")) {
        stmt.setBinaryStream(1, PlayerDatabase.uuidToStream(uuid));
        try (ResultSet rs = stmt.executeQuery()) {
          if (rs.next())
            return new SevensPlayer(this, rs.getInt("id"), rs.getInt("points"));
        }
        try (PreparedStatement stmt2 = c.prepareStatement("INSERT INTO svns_players (uuid, points) VALUES(?, 0)", Statement.RETURN_GENERATED_KEYS)) {
          stmt2.setBinaryStream(1, PlayerDatabase.uuidToStream(uuid));
          int rows = stmt2.executeUpdate();
          if (rows == 0)
            throw new SQLException("Creating player failed!");
          try (ResultSet rs = stmt2.getGeneratedKeys()) {
            if (rs.next())
              return new SevensPlayer(this, rs.getInt(1), 0);
            else
              throw new SQLException("Creating player failed!");
          }
        }
      }
    }
  }

  @Override
  public Record fetchRecord(SevensPlayer player1, SevensPlayer player2) throws SQLException {
    final String selectRecordStmtStr = "SELECT kills, deaths FROM svns_record WHERE player1=? AND player2=? FOR UPDATE";
    final String insertRecordStmtStr = "INSERT INTO svns_record (player1, player2, kills, deaths) VALUES (?, ?, 0, 0)";
    if (player1.id > player2.id) {
      SevensPlayer tmp = player1;
      player1 = player2;
      player2 = tmp;
    }
    try (Connection c = this.manager.getConnection();
         TransactionGuard guard = new TransactionGuard(c);
         PreparedStatement selectStmt = c.prepareStatement(selectRecordStmtStr)) {
      selectStmt.setInt(1, player1.id);
      selectStmt.setInt(2, player2.id);
      try (ResultSet rs = selectStmt.executeQuery()) {
        if (rs.next())
          return new Record(player1, player2, rs.getInt(1), rs.getInt(2));
      }
      try (PreparedStatement insertStmt = c.prepareStatement(insertRecordStmtStr)) {
        insertStmt.setInt(1, player1.id);
        insertStmt.setInt(2, player2.id);
        int rows = insertStmt.executeUpdate();
        if (rows == 0)
          throw new SQLException("Creating record failed!");
        return new Record(player1, player2, 0, 0);
      }
    }
  }

  @Override
  public void updateRecord(Record record, int newKills, int newDeaths) throws SQLException {
    final String updateRecordStmt = "UPDATE svns_record SET kills=kills+?, deaths=deaths+? WHERE player1=? AND player2=?";
    try (Connection c = this.manager.getConnection();
         PreparedStatement stmt = c.prepareStatement(updateRecordStmt)) {
      stmt.setInt(1, newKills);
      stmt.setInt(2, newDeaths);
      stmt.setInt(3, record.player1().id);
      stmt.setInt(4, record.player2().id);
      int rows = stmt.executeUpdate();
      if (rows == 0)
        throw new SQLException("Updating record failed!");
    }
  }

  @Override
  public void updateScore(SevensPlayer player, int addScore) throws Exception {
    final String updateScoreStmt = "UPDATE svns_players SET points=points+? WHERE id=?";
    try (Connection c = this.manager.getConnection();
         PreparedStatement stmt = c.prepareStatement(updateScoreStmt)) {
      stmt.setInt(1, addScore);
      stmt.setInt(2, player.id);
      int rows = stmt.executeUpdate();
      if (rows == 0)
        throw new SQLException("Updating score failed!");
    }
  }
}
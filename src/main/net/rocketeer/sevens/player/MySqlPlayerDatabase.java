package net.rocketeer.sevens.player;

import com.mysql.jdbc.Statement;
import net.rocketeer.sevens.database.DatabaseManager;
import net.rocketeer.sevens.database.TransactionGuard;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class MySqlPlayerDatabase implements PlayerDatabase {
  private final DatabaseManager manager;

  public MySqlPlayerDatabase(DatabaseManager manager) {
    this.manager = manager;
  }

  public SevensPlayer findPlayer(UUID uuid, boolean createIfNotExists) throws Exception {
    try (Connection c = this.manager.getConnection();
         TransactionGuard<SevensPlayer> guard = new TransactionGuard<>(c, () ->  {
      try (PreparedStatement stmt = c.prepareStatement("SELECT * FROM svns_players WHERE uuid=? FOR UPDATE")) {
        stmt.setBinaryStream(1, PlayerDatabase.uuidToStream(uuid));
        try (ResultSet rs = stmt.executeQuery()) {
          if (rs.next())
            return new SevensPlayer(this, uuid, rs.getInt("id"), rs.getInt("points"), 25, 8.33);
          else if (!createIfNotExists)
            return null;
        }
        try (PreparedStatement stmt2 = c.prepareStatement("INSERT INTO svns_players (uuid, points) VALUES(?, 0)", Statement.RETURN_GENERATED_KEYS)) {
          stmt2.setBinaryStream(1, PlayerDatabase.uuidToStream(uuid));
          int rows = stmt2.executeUpdate();
          if (rows == 0)
            throw new SQLException("Creating player failed!");
          try (ResultSet rs = stmt2.getGeneratedKeys()) {
            if (rs.next())
              return new SevensPlayer(this, uuid, rs.getInt(1), 0, 25, 8.33);
            else
              throw new SQLException("Creating player failed!");
          }
        }
      }
    })) {
      return guard.run();
    }
  }

  @Override
  public Record fetchRecord(SevensPlayer player1, SevensPlayer player2) throws Exception {
    final String selectRecordStmtStr = "SELECT kills, deaths FROM svns_record WHERE player1=? AND player2=? FOR UPDATE";
    final String insertRecordStmtStr = "INSERT INTO svns_record (player1, player2, kills, deaths) VALUES (?, ?, 0, 0)";
    if (player1.id > player2.id) {
      SevensPlayer tmp = player1;
      player1 = player2;
      player2 = tmp;
    }
    final SevensPlayer finalPlayer = player1;
    final SevensPlayer finalPlayer2 = player2;
    try (Connection c = this.manager.getConnection();
         TransactionGuard<Record> guard = new TransactionGuard<>(c, () -> {
      try (PreparedStatement selectStmt = c.prepareStatement(selectRecordStmtStr)) {
        selectStmt.setInt(1, finalPlayer.id);
        selectStmt.setInt(2, finalPlayer2.id);
        try (ResultSet rs = selectStmt.executeQuery()) {
          if (rs.next())
            return new Record(finalPlayer, finalPlayer2, rs.getInt(1), rs.getInt(2));
        }
        try (PreparedStatement insertStmt = c.prepareStatement(insertRecordStmtStr)) {
          insertStmt.setInt(1, finalPlayer.id);
          insertStmt.setInt(2, finalPlayer2.id);
          int rows = insertStmt.executeUpdate();
          if (rows == 0)
            throw new SQLException("Creating record failed!");
          return new Record(finalPlayer, finalPlayer2, 0, 0);
        }
      }
    })) {
      return guard.run();
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

  @Override
  public List<SevensPlayer> fetchTopPlayers(int begin, int nPlayers) throws Exception {
    final String stmtStr = "SELECT * FROM svns_players ORDER BY points DESC LIMIT ?, ?";
    try (Connection c = this.manager.getConnection();
         PreparedStatement stmt = c.prepareStatement(stmtStr)) {
      stmt.setInt(1, begin);
      stmt.setInt(2, nPlayers);
      try (ResultSet rs = stmt.executeQuery()) {
        List<SevensPlayer> players = new LinkedList<>();
        while (rs.next()) {
          UUID uuid = PlayerDatabase.streamToUuid(rs.getBinaryStream("uuid"));
          players.add(new SevensPlayer(this, uuid, rs.getInt("id"), rs.getInt("points"),
              rs.getDouble("rating_mu"), rs.getDouble("rating_sigma")));
        }
        return players;
      }
    }
  }

  @Override
  public void resetAllScores() throws Exception {
    try (Connection c = this.manager.getConnection();
         java.sql.Statement stmt = c.createStatement()) {
      stmt.execute("UPDATE svns_players SET points=0");
    }
  }

  @Override
  public void updateRating(SevensPlayer player, double muDelta, double sigmaDelta) throws Exception {
    try (Connection c = this.manager.getConnection();
         PreparedStatement stmt = c.prepareStatement("UPDATE svns_players SET rating_sigma=rating_sigma+?, " +
             "rating_mu=rating_mu+?, rating=? WHERE id=?")) {
      stmt.setDouble(1, sigmaDelta);
      stmt.setDouble(2, muDelta);
      stmt.setDouble(3, player.rating());
      stmt.setInt(4, player.id);
      int rows = stmt.executeUpdate();
      if (rows == 0)
        throw new SQLException("Failed to update sigma");
    }
  }

  @Override
  public PlayerRank computeRank(SevensPlayer player) throws Exception {

    return null;
  }
}

package net.rocketeer.sevens.player;

public class SevensPlayer {
  final int id;
  private final PlayerDatabase database;

  SevensPlayer(PlayerDatabase database, int id) {
    this.id = id;
    this.database = database;
  }

  public void addKillAgainst(SevensPlayer other) throws Exception {
    Record record = this.database.fetchRecord(this, other);
    this.database.updateRecord(record, 1, 0);
  }

  public void addDeathAgainst(SevensPlayer other) throws Exception {
    Record record = this.database.fetchRecord(this, other);
    this.database.updateRecord(record, 0, 1);
  }
}

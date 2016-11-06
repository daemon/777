package net.rocketeer.sevens.player;

public class SevensPlayer {
  final int id;
  private final PlayerDatabase database;

  SevensPlayer(PlayerDatabase database, int id) {
    this.id = id;
    this.database = database;
  }

  public Record fetchRecord(SevensPlayer other) {
    try {
      return this.database.fetchRecord(this, other);
    } catch (Exception e) {
      return null;
    }
  }

  public void addKillAgainst(SevensPlayer other) {
    try {
      Record record = this.database.fetchRecord(this, other);
      this.database.updateRecord(record, 1, 0);
    } catch (Exception ignored) {}
  }

  public void addDeathAgainst(SevensPlayer other) {
    try {
      Record record = this.database.fetchRecord(this, other);
      this.database.updateRecord(record, 0, 1);
    } catch (Exception ignored) {}
  }
}

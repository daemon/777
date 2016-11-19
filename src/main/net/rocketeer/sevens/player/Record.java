package net.rocketeer.sevens.player;

public class Record {
  private final SevensPlayer player1;
  private final SevensPlayer player2;
  private int kills;
  private int deaths;

  Record(SevensPlayer player1, SevensPlayer player2, int kills, int deaths) {
    if (player1.id > player2.id) {
      this.player1 = player2;
      this.player2 = player1;
      this.kills = deaths;
      this.deaths = kills;
    } else {
      this.player1 = player1;
      this.player2 = player2;
      this.kills = kills;
      this.deaths = deaths;
    }
  }

  public int deaths() {
    return this.deaths;
  }

  public int kills() {
    return this.kills;
  }

  public SevensPlayer player1() {
    return this.player1;
  }

  public SevensPlayer player2() {
    return this.player2;
  }
}

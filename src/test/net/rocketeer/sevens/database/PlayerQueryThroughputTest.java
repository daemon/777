package net.rocketeer.sevens.database;

import net.rocketeer.sevens.player.PlayerDatabase;

import java.util.UUID;

public class PlayerQueryThroughputTest {
  private final PlayerDatabase database;
  private final int nQueries;

  public PlayerQueryThroughputTest(PlayerDatabase database, int nQueries) {
    this.database = database;
    this.nQueries = nQueries;
  }
  public void run() throws Exception {
    long a = System.currentTimeMillis();
    for (int i = 0; i < this.nQueries; ++i)
      this.database.findPlayer(UUID.randomUUID(), false);
    double seconds = (System.currentTimeMillis() - a) / 1000.0;
    System.out.println("Queries per second: " + this.nQueries / seconds);
  }
}

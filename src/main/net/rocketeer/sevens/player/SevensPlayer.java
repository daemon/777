package net.rocketeer.sevens.player;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class SevensPlayer {
  final int id;
  private final PlayerDatabase database;
  private final int score;
  private final UUID uuid;
  private final int nPlays;
  private final boolean isEnabled;
  private double sigma;
  private double mu;

  SevensPlayer(PlayerDatabase database, UUID uuid, int id, int score, double mu, double sigma, int nPlays, boolean isEnabled) {
    this.id = id;
    this.score = score;
    this.database = database;
    this.uuid = uuid;
    this.mu = mu;
    this.sigma = sigma;
    this.nPlays = nPlays;
    this.isEnabled = isEnabled;
  }

  public boolean isEnabled() {
    return this.isEnabled;
  }

  public int nPlays() {
    return this.nPlays;
  }

  public UUID uuid() {
    return this.uuid;
  }

  public int id() {
    return this.id;
  }

  public int score() {
    return this.score;
  }

  public double sigma() {
    return this.sigma;
  }

  public double mu() {
    return this.mu;
  }

  public double rating() {
    return this.mu() - 3 * this.sigma;
  }

  public void addRating(double muDelta, double sigmaDelta) throws Exception {
    this.sigma += sigmaDelta;
    this.mu += muDelta;
    this.database.updateRating(this, muDelta, sigmaDelta);
  }

  public void addScore(int points) throws Exception {
    this.database.updateScore(this, points);
  }

  public void addKillAgainst(SevensPlayer other) throws Exception {
    Record record = this.database.fetchRecord(this, other);
    if (record.player1().id() == this.id)
      this.database.updateRecord(record, 1, 0);
    else
      this.database.updateRecord(record, 0, 1);
  }

  public void addDeathAgainst(SevensPlayer other) throws Exception {
    Record record = this.database.fetchRecord(this, other);
    if (record.player1().id() == this.id)
      this.database.updateRecord(record, 0, 1);
    else
      this.database.updateRecord(record, 1, 0);
  }
}

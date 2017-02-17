package net.rocketeer.sevens.game.kotr;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PongGameManager {
  private enum GameState {IDLE, IN_PROGRESS, FINISHED};
  private final JavaPlugin plugin;
  private Player player1;
  private Player player2;
  private int player1score = 0;
  private int player2score = 0;
  private GameState state = GameState.IDLE;

  public PongGameManager(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  public void addScore(Player player) {
    if (this.state != GameState.IN_PROGRESS)
      return;
    if (player.equals(this.player1))
      ++this.player1score;
    else if (player.equals(this.player2))
      ++this.player2score;
    if (this.player1score == 10 || this.player2score == 10)
      this.finish();
  }

  public void finish() {
    if (this.state != GameState.IN_PROGRESS)
      return;
  }

  public GameState state() {
    return this.state;
  }

  public void start(Player player1, Player player2) {
    if (this.state != GameState.IDLE)
      return;
    this.state = GameState.IN_PROGRESS;
    this.player1score = 0;
    this.player2score = 0;
  }

  static class Paddle {
    public int x, y;
    public int dx, dy;
    public Paddle(int x, int y) {
      this.x = x;
      this.y = y;
      this.dx = this.dy = 0;
    }
  }

  static class Ball {
    public int x, y;
    public int dx, dy;
    public Ball(int x, int y) {
      this.x = x;
      this.y = y;
      this.dx = this.dy = 0;
    }
  }
}

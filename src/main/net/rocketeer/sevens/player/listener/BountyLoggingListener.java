package net.rocketeer.sevens.player.listener;

import net.rocketeer.sevens.game.bounty.BountyRegistry;
import net.rocketeer.sevens.game.bounty.BountyRewardEvent;
import net.rocketeer.sevens.player.PlayerDatabase;
import net.rocketeer.sevens.player.SevensPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class BountyLoggingListener implements Listener {
  private final PlayerDatabase database;
  private final JavaPlugin plugin;

  public BountyLoggingListener(JavaPlugin plugin, PlayerDatabase database) {
    this.database = database;
    this.plugin = plugin;
  }

  private Map<Player, Long> killTimestamps = new HashMap<>();
  private Map<Player, Double> killMultipliers = new HashMap<>();
  private long lastKilled = System.currentTimeMillis();

  private int getPoints(BountyRewardEvent event) {
    if (event.cause() != BountyRewardEvent.Cause.KILL)
      return event.bountyReward();
    if (System.currentTimeMillis() - this.lastKilled > 60000) {
      this.killTimestamps.clear();
      this.killMultipliers.clear();
    }
    this.lastKilled = System.currentTimeMillis();
    Player player = event.player();
    Long timestamp = this.killTimestamps.get(player);
    if (timestamp == null || System.currentTimeMillis() - timestamp > 60000) {
      this.killTimestamps.put(player, System.currentTimeMillis());
      this.killMultipliers.put(player, 1.0);
      return event.bountyReward();
    }
    Double multiplier = this.killMultipliers.get(player);
    multiplier *= 0.75;
    this.killMultipliers.put(player, multiplier);
    return (int) (multiplier * event.bountyReward());
  }

  @EventHandler
  public void onBountyRewardEvent(BountyRewardEvent event) {
    final int points = this.getPoints(event);
    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
      try {
        SevensPlayer player = this.database.findPlayer(event.player().getUniqueId(), true);
        player.addScore(points);
        Bukkit.getScheduler().runTask(this.plugin, () -> {
          event.player().sendMessage("You've earned " + ChatColor.GOLD + points + ChatColor.WHITE + " points");
        });
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}

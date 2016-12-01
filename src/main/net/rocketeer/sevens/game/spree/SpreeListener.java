package net.rocketeer.sevens.game.spree;

import net.rocketeer.sevens.game.bounty.BountyRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class SpreeListener implements Listener {
  private final BountyRegistry bountyRegistry;
  private final SpreeConfig config;
  private Map<Player, Long> killTimestamps = new HashMap<>();
  private Map<Player, Double> killMultiplier = new HashMap<>();
  private long lastKilled = System.currentTimeMillis();

  public SpreeListener(SpreeConfig config, BountyRegistry bountyRegistry) {
    this.bountyRegistry = bountyRegistry;
    this.config = config;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    this.bountyRegistry.initBounty(event.getPlayer());
  }

  @EventHandler
  public void onPlayerLeave(PlayerQuitEvent event) {
    this.bountyRegistry.removePlayer(event.getPlayer());
  }

  // TODO move code from killTimestamps, killMultipliers etc into a new registry
  @EventHandler(priority=EventPriority.MONITOR)
  public void onSpreeChange(SpreeChangeEvent event) {
    if (event.spree() == 0) {
      String message = SpreeConfig.formatString(this.config.spreeEndedMessage(), event.player().getName(), event.oldSpree());
      Bukkit.broadcastMessage(message);
      return;
    }
    if (System.currentTimeMillis() - this.lastKilled > 60000) {
      this.killTimestamps.clear();
      this.killMultiplier.clear();
    }
    this.lastKilled = System.currentTimeMillis();
    Player player = event.player();
    Long timestamp = this.killTimestamps.get(player);
    if (timestamp == null || System.currentTimeMillis() - timestamp > 60000) {
      this.killTimestamps.put(player, System.currentTimeMillis());
      this.killMultiplier.put(player, 1.0);
    }
    Double multiplier = this.killMultiplier.get(player);
    multiplier *= 0.75;
    String message = this.config.spreeMessages().get(event.spree());
    int bounty = this.bountyRegistry.getAttribute(event.player());
    this.bountyRegistry.setAttribute(event.player(), (int) (bounty + multiplier * this.config.bountyIncrement()));
    if (message == null && event.spree() < this.config.maxMessageBounty())
      return;
    else if (event.spree() > this.config.maxMessageBounty() && event.spree() % 5 == 0)
      message = this.config.spreeMessages().get(this.config.maxMessageBounty());
    message = SpreeConfig.formatString(message, event.player().getName(), event.spree());
    Bukkit.broadcastMessage(message);
  }
}

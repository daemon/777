package net.rocketeer.sevens.game.spree;

import net.rocketeer.sevens.game.bounty.BountyRegistry;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SpreeListener implements Listener {
  private final BountyRegistry bountyRegistry;
  private final SpreeConfig config;

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

  @EventHandler(priority=EventPriority.MONITOR)
  public void onSpreeChange(SpreeChangeEvent event) {
    if (event.spree() == 0) {
      String message = SpreeConfig.formatString(this.config.spreeEndedMessage(), event.player().getName(), event.oldSpree());
      Bukkit.broadcastMessage(message);
      return;
    }
    String message = this.config.spreeMessages().get(event.spree());
    int bounty = this.bountyRegistry.getAttribute(event.player());
    this.bountyRegistry.setAttribute(event.player(), (int) (this.config.bountyMultiplier() * bounty + this.config.bountyIncrement()));
    if (message == null && event.spree() < this.config.maxMessageBounty())
      return;
    else if (event.spree() > this.config.maxMessageBounty() && event.spree() % 5 == 0) {
      message = this.config.spreeMessages().get(this.config.maxMessageBounty());
    }
    message = SpreeConfig.formatString(message, event.player().getName(), event.spree());
    Bukkit.broadcastMessage(message);
  }
}

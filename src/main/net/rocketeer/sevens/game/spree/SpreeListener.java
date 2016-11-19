package net.rocketeer.sevens.game.spree;

import net.rocketeer.sevens.game.AttributeRegistry;
import net.rocketeer.sevens.game.bounty.BountyRegistry;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpreeListener implements Listener {
  private final BountyRegistry bountyRegistry;
  private final SpreeConfig config;

  public SpreeListener(SpreeConfig config, BountyRegistry bountyRegistry) {
    this.bountyRegistry = bountyRegistry;
    this.config = config;
  }

  @EventHandler
  public void onSpreeChange(SpreeChangeEvent event) {
    if (event.spree() == 0) {
      String message = SpreeConfig.formatString(this.config.spreeEndedMessage(), event.player().getName(), event.oldSpree());
      this.bountyRegistry.initBounty(event.player());
      Bukkit.broadcastMessage(message);
      return;
    }
    String message = this.config.spreeMessages().get(event.spree());
    Integer bounty = this.bountyRegistry.getAttribute(event.player());
    if (bounty == null) {
      this.bountyRegistry.initBounty(event.player());
      bounty = this.bountyRegistry.getAttribute(event.player());
    }
    this.bountyRegistry.setAttribute(event.player(), (int) (this.config.bountyMultiplier() * bounty + this.config.bountyIncrement()));
    if (message == null)
      return;
    message = SpreeConfig.formatString(message, event.player().getName(), event.spree());
    Bukkit.broadcastMessage(message);
  }
}

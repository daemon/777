package net.rocketeer.sevens.game.bounty;

import net.rocketeer.sevens.game.name.NameTag;
import net.rocketeer.sevens.game.name.NameTagRegistry;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BountyNameTagListener implements Listener {
  private final NameTagRegistry registry;

  public BountyNameTagListener(NameTagRegistry registry) {
    this.registry = registry;
  }

  @EventHandler
  public void onBountyChange(BountyChangeEvent event) {
    String bountyStr = "(" + ChatColor.GOLD + event.bounty() + ChatColor.WHITE + ")";
    NameTag tag = this.registry.getAttribute(event.player());
    if (tag == null) {
      tag = this.registry.registerNameTag(event.player(), bountyStr);
    } else {
      tag.setTag(bountyStr);
    }
  }
}

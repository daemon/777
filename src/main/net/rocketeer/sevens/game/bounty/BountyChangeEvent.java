package net.rocketeer.sevens.game.bounty;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BountyChangeEvent extends Event {
  private static final HandlerList handlers = new HandlerList();
  private final int newBounty;
  private final Player player;
  private final int oldBounty;

  BountyChangeEvent(Player player, int newBounty, int oldBounty) {
    this.newBounty = newBounty;
    this.oldBounty = oldBounty;
    this.player = player;
  }

  public int bounty() {
    return this.newBounty;
  }

  public int oldBounty() { return this.oldBounty; }

  public Player player() {
    return this.player;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}

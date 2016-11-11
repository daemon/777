package net.rocketeer.sevens.game.bounty;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BountyChangeEvent extends Event {
  private static final HandlerList handlers = new HandlerList();
  private final int newBounty;
  private final Player player;

  BountyChangeEvent(Player player, int newBounty) {
    this.newBounty = newBounty;
    this.player = player;
  }

  public int newBounty() {
    return this.newBounty;
  }

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

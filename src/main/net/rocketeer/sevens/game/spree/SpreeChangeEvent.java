package net.rocketeer.sevens.game.spree;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpreeChangeEvent extends Event {
  private static final HandlerList handlers = new HandlerList();
  private final int newBounty;
  private final Player player;

  SpreeChangeEvent(Player player, int newSpree) {
    this.newBounty = newSpree;
    this.player = player;
  }

  public int newSpree() {
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

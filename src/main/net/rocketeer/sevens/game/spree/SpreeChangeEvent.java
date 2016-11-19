package net.rocketeer.sevens.game.spree;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpreeChangeEvent extends Event {
  private static final HandlerList handlers = new HandlerList();
  private final int spree;
  private final Player player;
  private final int oldSpree;

  SpreeChangeEvent(Player player, int newSpree, int oldSpree) {
    this.spree = newSpree;
    this.oldSpree = oldSpree;
    this.player = player;
  }

  public int spree() {
    return this.spree;
  }

  public int oldSpree() {
    return this.oldSpree;
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

package net.rocketeer.sevens.game.bounty;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BountyAddEvent extends Event {
  private static final HandlerList handlers = new HandlerList();
  private final int bountyDiff;
  private final Player player;

  BountyAddEvent(Player player, int bountyDiff) {
    this.bountyDiff = bountyDiff;
    this.player = player;
  }

  public int bountyDiff() {
    return this.bountyDiff;
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

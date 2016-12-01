package net.rocketeer.sevens.game.bounty;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BountyRewardEvent extends Event {
  public enum Cause {KILL}
  private final Cause cause;
  private static final HandlerList handlers = new HandlerList();
  private int bountyReward;
  private Player player;

  BountyRewardEvent(Player player, int bountyReward, Cause cause) {
    this.player = player;
    this.bountyReward = bountyReward;
    this.cause = cause;
  }

  public int bountyReward() { return this.bountyReward; }

  public Player player() {
    return this.player;
  }

  public Cause cause() {
    return this.cause;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}

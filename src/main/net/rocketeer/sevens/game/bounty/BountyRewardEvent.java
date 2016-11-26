package net.rocketeer.sevens.game.bounty;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BountyRewardEvent extends Event {
  private static final HandlerList handlers = new HandlerList();
  private final int bountyReward;
  private final Player player;

  BountyRewardEvent(Player player, int bountyReward) {
    this.player = player;
    this.bountyReward = bountyReward;
  }

  public int bountyReward() { return this.bountyReward; }

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

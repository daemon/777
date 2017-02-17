package net.rocketeer.sevens.game.name;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NameTagChangeEvent extends Event {
  private static final HandlerList handlers = new HandlerList();
  private final NameTag tag;

  public NameTagChangeEvent(NameTag tag) {
    this.tag = tag;
  }

  public NameTag tag() {
    return this.tag;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}

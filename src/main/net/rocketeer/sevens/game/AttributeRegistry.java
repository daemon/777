package net.rocketeer.sevens.game;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AttributeRegistry<T> {
  private final String name;
  private boolean isActive = true;
  public AttributeRegistry(String name) {
    this.name = name;
  }

  public String name() {
    return this.name;
  }

  public boolean isActive() {
    return this.isActive;
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
    if (!isActive)
      this.disable();
    else
      this.enable();
  }

  protected void disable() {};
  protected void enable() {};
  public void clear() {}
  public abstract void init(JavaPlugin plugin);
  public T getAttribute(Player player) { return null; }
  public void setAttribute(Player player, T attribute) { return; }
}

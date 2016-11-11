package net.rocketeer.sevens.game;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AttributeRegistry {
  private final String name;
  private boolean isActive;
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
  }

  public void clear() {}
  public abstract void init(JavaPlugin plugin);
  public Integer getInteger(Player player) { return null; }
  public String getString(Player player) { return null; }
}

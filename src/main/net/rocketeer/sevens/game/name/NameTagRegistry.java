package net.rocketeer.sevens.game.name;

import net.rocketeer.sevens.game.AttributeRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class NameTagRegistry extends AttributeRegistry {
  private final Map<Player, NameTag> nameTagMap = new HashMap<>();
  private JavaPlugin plugin;

  public NameTagRegistry(String name) {
    super(name);
  }

  public void registerNameTag(Player player, String name) {
    NameTag tag = new NameTag(player, name);
    tag.create();
    this.nameTagMap.put(player, tag);
  }

  public void unregisterFakeName(Player player) {
    NameTag tag = this.nameTagMap.get(player);
    if (tag == null)
      return;
    this.nameTagMap.remove(player);
    tag.destroy();
  }

  @Override
  public void clear() {
    this.nameTagMap.forEach((player, tag) -> tag.destroy());
    this.nameTagMap.clear();
  }

  @Override
  public void init(JavaPlugin plugin) {}

  @Override
  public String getString(Player player) {
    NameTag tag = this.nameTagMap.get(player);
    if (tag == null)
      return null;
    return tag.toString();
  }

  private class NameTagListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

    }
  }
}

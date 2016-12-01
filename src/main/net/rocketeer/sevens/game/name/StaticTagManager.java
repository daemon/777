package net.rocketeer.sevens.game.name;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class StaticTagManager {
  private final JavaPlugin plugin;
  private Set<StaticTag> tags = new HashSet<>();

  public StaticTagManager(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  public StaticTag spawnTag(Location location, String text) {
    StaticTag tag = new StaticTag(location, text);
    this.tags.add(tag);
    tag.spawn();
    return tag;
  }

  public StaticTag spawnTemporaryTag(Location location, String text, long durationTicks) {
    StaticTag tag = new StaticTag(location, text);
    this.tags.add(tag);
    tag.spawn();
    Bukkit.getScheduler().runTaskLater(this.plugin, tag::despawn, durationTicks);
    return tag;
  }

  private void doNextFrame(StaticTag tag, long intervalTicks, AnimateTagListener listener, int frame) {
    AnimateTagListener.Result result = listener.nextFrame(tag, frame);
    switch (result) {
    case PROCEED:
      Bukkit.getScheduler().runTaskLater(this.plugin, () -> doNextFrame(tag, intervalTicks, listener, frame + 1), intervalTicks);
      break;
    case RESTART:
      Bukkit.getScheduler().runTaskLater(this.plugin, () -> doNextFrame(tag, intervalTicks, listener, 0), intervalTicks);
      break;
    case STOP:
      tag.despawn();
      this.tags.remove(tag);
    }
  }

  public void spawnAnimatedTag(Location location, long intervalTicks, AnimateTagListener listener) {
    StaticTag tag = new StaticTag(location);
    tag.spawn();
    this.tags.add(tag);
    this.doNextFrame(tag, intervalTicks, listener, 0);
  }

  public void despawnAll() {
    this.tags.forEach(StaticTag::despawn);
  }

  @FunctionalInterface
  public interface AnimateTagListener {
    enum Result { PROCEED, RESTART, STOP }
    Result nextFrame(StaticTag tag, int frame);
  }
}

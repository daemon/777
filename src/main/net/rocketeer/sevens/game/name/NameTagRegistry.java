package net.rocketeer.sevens.game.name;

import net.rocketeer.sevens.game.AttributeRegistry;
import net.rocketeer.sevens.game.SpatialHashMap;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public class NameTagRegistry extends AttributeRegistry<NameTag> {
  private final Map<Player, NameTag> nameTagMap = new HashMap<>();
  private final SpatialHashMap<Player> players = new SpatialHashMap<>();
  private final Map<Player, Location> lastLocations = new HashMap<>();
  private final Set<String> worlds = new HashSet<>();
  private JavaPlugin plugin;

  public NameTagRegistry(String name) {
    super(name);
  }

  public NameTag registerNameTag(Player player, String name) {
    NameTag tag = new NameTag(player, name);
    this.nameTagMap.put(player, tag);
    this.updateTagLocally(tag);
    return tag;
  }

  public void unregisterNameTag(Player player) {
    NameTag tag = this.nameTagMap.get(player);
    if (tag == null)
      return;
    tag.destroy();
    this.nameTagMap.remove(player);
  }

  @Override
  public NameTag getAttribute(Player player) {
    return this.nameTagMap.get(player);
  }

  @Override
  public void clear() {
    this.nameTagMap.forEach((player, tag) -> tag.destroy());
    this.nameTagMap.clear();
  }

  @Override
  public void init(JavaPlugin plugin) {
    Bukkit.getPluginManager().registerEvents(new NameTagListener(), plugin);
    List<String> worlds = plugin.getConfig().getStringList("worlds");
    if (worlds == null)
      return;
    worlds = worlds.stream().map(String::toLowerCase).collect(Collectors.toList());
    this.worlds.addAll(worlds);
  }

  @Override
  protected void disable() {
    this.nameTagMap.forEach((player, tag) -> tag.destroy());
  }

  private void updateTagLocally(NameTag tag) {
    if (!isActive())
      return;
    Player target = tag.owner();

    boolean invisible = target.hasPotionEffect(PotionEffectType.INVISIBILITY);
    if (!this.worlds.contains(target.getWorld().getName().toLowerCase()) || target.getGameMode() != GameMode.SURVIVAL || invisible) {
      this.stopTracking(target);
      return;
    }
    Location loc = target.getLocation();
    Set<Player> nearbyPlayers = players.getWithin(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 64);
    List<Player> removePlayers = new LinkedList<>();
    for (Player p : tag.visiblePlayers()) {
      if (!p.getWorld().equals(target.getWorld()) || p.isDead() || target.isDead() || !target.isOnline()) {
        removePlayers.add(p);
        nearbyPlayers.remove(p);
        continue;
      }
      if (nearbyPlayers.contains(p)) {
        tag.update(p);
        nearbyPlayers.remove(p);
      } else
        removePlayers.add(p);
    }
    nearbyPlayers.forEach(tag::create);
    removePlayers.forEach(tag::destroy);
  }

  private void stopTracking(Player player) {
    Location loc = player.getLocation();
    this.players.remove(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    loc = lastLocations.remove(player);
    if (loc != null)
      this.players.remove(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

    unregisterNameTag(player);
  }

  private class NameTagListener implements Listener {
    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
      Player player = event.getPlayer();
      NameTag tag = nameTagMap.get(player);
      if (tag == null)
        return;
      updateTagLocally(tag);
    }

    @EventHandler
    public void onChangeWorldEvent(PlayerChangedWorldEvent event) {
      if (worlds.contains(event.getPlayer().getWorld().getName().toLowerCase()))
        return;
      stopTracking(event.getPlayer());
    }

    @EventHandler
    public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
      if (event.getNewGameMode() != GameMode.SURVIVAL)
        stopTracking(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
      Player player = event.getEntity();
      stopTracking(player);
    }

    private int manhattanDistance(Location a, Location b) {
      return Math.abs(a.getBlockX() - b.getBlockX()) + Math.abs(a.getBlockY() - b.getBlockY()) + Math.abs(a.getBlockZ() - b.getBlockZ());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      stopTracking(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
      if (!isActive())
        return;
      Player player = event.getPlayer();
      if (player.isDead())
        return;
      Location loc = player.getLocation();
      players.put(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), player);
      Location lastLoc = lastLocations.get(player);
      if (lastLoc == null) {
        lastLocations.put(player, loc.clone());
        return;
      }
      if (this.manhattanDistance(lastLoc, loc) > 0) {
        players.remove(lastLoc.getBlockX(), lastLoc.getBlockY(), lastLoc.getBlockZ());
        lastLocations.put(player, loc.clone());
      }
      NameTag tag = nameTagMap.get(player);
      if (tag == null)
        return;
      updateTagLocally(tag);
    }
  }
}

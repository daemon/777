package net.rocketeer.sevens.game.bounty;

import net.rocketeer.sevens.game.AttributeRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class BountyRegistry extends AttributeRegistry<Integer> {
  private Map<Player, Integer> playerToBounty = new HashMap<>();
  private int defaultBounty;
  private JavaPlugin plugin;

  public BountyRegistry(String name) {
    super(name);
  }

  @Override
  public void init(JavaPlugin plugin) {
    this.defaultBounty = plugin.getConfig().getInt("default-bounty", 10);
    Bukkit.getPluginManager().registerEvents(new BountyListener(), plugin);
    this.plugin = plugin;
  }

  public void initBounty(Player player) {
    this.playerToBounty.put(player, this.defaultBounty);
    Bukkit.getPluginManager().callEvent(new BountyChangeEvent(player, this.defaultBounty, 0));
  }

  public void removePlayer(Player player) {
    this.playerToBounty.remove(player);
  }

  @Override
  public void setAttribute(Player player, Integer newBounty) {
    Integer oldBounty = playerToBounty.remove(player);
    if (oldBounty == null)
      oldBounty = this.defaultBounty;
    this.playerToBounty.put(player, newBounty);
    Bukkit.getPluginManager().callEvent(new BountyChangeEvent(player, newBounty, oldBounty));
  }

  @Override
  public Integer getAttribute(Player player) {
    return this.playerToBounty.get(player);
  }

  @Override
  public void clear() {
    this.playerToBounty.clear();
  }

  private class BountyListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
      initBounty(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
      playerToBounty.remove(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
      Player killer = event.getEntity().getKiller();
      Player player = event.getEntity();
      int bounty = playerToBounty.get(player);
      initBounty(player);
      if (killer == null)
        return;
      if (player.equals(killer))
        return;
      Bukkit.getPluginManager().callEvent(new BountyRewardEvent(killer, bounty, BountyRewardEvent.Cause.KILL));
    }
  }
}

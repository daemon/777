package net.rocketeer.sevens.game.bounty;

import net.rocketeer.sevens.game.AttributeRegistry;
import net.rocketeer.sevens.player.PlayerDatabase;
import net.rocketeer.sevens.player.SevensPlayer;
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
  private final PlayerDatabase database;
  private Map<Player, Integer> playerToBounty = new HashMap<>();
  private double defaultBountyPower;
  private JavaPlugin plugin;

  public BountyRegistry(String name, PlayerDatabase database) {
    super(name);
    this.database = database;
  }

  @Override
  public void init(JavaPlugin plugin) {
    this.defaultBountyPower = plugin.getConfig().getDouble("default-bounty-power", 1.5);
    Bukkit.getPluginManager().registerEvents(new BountyListener(), plugin);
    this.plugin = plugin;
  }

  private int computeDefaultBounty(Player player) {
    SevensPlayer sPlayer;
    try {
      sPlayer = this.database.findPlayer(player.getUniqueId(), true);
    } catch (Exception e) {
      return 10;
    }
    return (int) Math.max(Math.pow(sPlayer.rating(), this.defaultBountyPower), 1);
  }

  public void initBounty(Player player) {
    int bounty = this.computeDefaultBounty(player);
    this.playerToBounty.put(player, bounty);
    Bukkit.getPluginManager().callEvent(new BountyChangeEvent(player, bounty, 0));
  }

  public void removePlayer(Player player) {
    this.playerToBounty.remove(player);
  }

  @Override
  public void setAttribute(Player player, Integer newBounty) {
    Integer oldBounty = playerToBounty.remove(player);
    if (oldBounty == null)
      oldBounty = this.computeDefaultBounty(player);
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

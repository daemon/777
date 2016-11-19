package net.rocketeer.sevens.game.bounty;

import net.rocketeer.sevens.game.AttributeRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
    this.plugin = plugin;
  }

  public void initBounty(Player player) {
    this.playerToBounty.put(player, this.defaultBounty);
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


}

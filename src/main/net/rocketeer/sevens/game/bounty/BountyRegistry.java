package net.rocketeer.sevens.game.bounty;

import net.rocketeer.sevens.game.AttributeRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class BountyRegistry extends AttributeRegistry {
  private Map<Player, Integer> playerToBounty = new HashMap<>();
  private int defaultBounty = 10;

  public BountyRegistry(String name) {
    super(name);
  }

  @Override
  public void init(JavaPlugin plugin) {
    Bukkit.getPluginManager().registerEvents(new BountyChangeListener(), plugin);
    this.defaultBounty = plugin.getConfig().getInt("default-bounty", 10);
  }

  @Override
  public Integer getInteger(Player player) {
    return this.playerToBounty.get(player);
  }

  @Override
  public void clear() {
    this.playerToBounty.clear();
  }

  public class BountyChangeListener implements Listener {
    @EventHandler
    public void onBountyChange(BountyChangeEvent event) {
      if (!isActive())
        return;
      playerToBounty.put(event.player(), event.newBounty());
    }
  }

  public class BountyAddListener implements Listener {
    @EventHandler
    public void onBountyAdd(BountyAddEvent event) {
      if (!isActive())
        return;
      if (!playerToBounty.containsKey(event.player()))
        playerToBounty.put(event.player(), defaultBounty);
      playerToBounty.put(event.player(), playerToBounty.get(event.player()) + event.bountyDiff());
    }
  }
}

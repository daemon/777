package net.rocketeer.sevens.game.spree;

import net.rocketeer.sevens.game.AttributeRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class SpreeRegistry extends AttributeRegistry<Integer> {
  private Map<Player, Integer> playerToSpree = new HashMap<>();
  private int minSpree;

  public SpreeRegistry(String name) {
    super(name);
  }

  @Override
  public void init(JavaPlugin plugin) {
    Bukkit.getPluginManager().registerEvents(new KillListener(), plugin);
    this.minSpree = plugin.getConfig().getConfigurationSection("spree").getInt("min-kills", 3);
  }

  @Override
  public Integer getAttribute(Player player) {
    return this.playerToSpree.get(player);
  }

  @Override
  public void clear() {
    this.playerToSpree.clear();
  }

  public class KillListener implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
      if (!playerToSpree.containsKey(event.getEntity())) {
        playerToSpree.put(event.getEntity(), 0);
      } else {
        int spree = playerToSpree.get(event.getEntity());
        playerToSpree.put(event.getEntity(), 0);
        if (spree >= minSpree)
          Bukkit.getPluginManager().callEvent(new SpreeChangeEvent(event.getEntity(), 0, spree));
      }
      Player killer = event.getEntity().getKiller();
      if (killer == null)
        return;
      if (!playerToSpree.containsKey(killer))
        playerToSpree.put(killer, 0);
      int currentSpree = playerToSpree.get(killer) + 1;
      playerToSpree.put(killer, currentSpree);
      if (currentSpree >= minSpree)
        Bukkit.getPluginManager().callEvent(new SpreeChangeEvent(killer, currentSpree, currentSpree - 1));
    }
  }
}

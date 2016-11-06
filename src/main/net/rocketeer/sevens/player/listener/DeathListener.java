package net.rocketeer.sevens.player.listener;

import net.rocketeer.sevens.player.PlayerDatabase;
import net.rocketeer.sevens.player.SevensPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class DeathListener implements Listener {
  private final PlayerDatabase database;
  private final JavaPlugin plugin;
  private final Set<String> trackedWorlds;

  public DeathListener(JavaPlugin plugin, PlayerDatabase database, Set<String> trackedWorlds) {
    this.database = database;
    this.plugin = plugin;
    this.trackedWorlds = trackedWorlds;
  }

  @EventHandler
  public void onDeathEvent(PlayerDeathEvent event) throws Exception {
    if (!this.trackedWorlds.contains(event.getEntity().getWorld().getName()))
      return;
    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
      try {
        SevensPlayer killer = this.database.findPlayer(event.getEntity().getKiller().getUniqueId());
        SevensPlayer target = this.database.findPlayer(event.getEntity().getUniqueId());
        killer.addKillAgainst(target);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}

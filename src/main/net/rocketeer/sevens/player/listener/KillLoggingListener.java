package net.rocketeer.sevens.player.listener;

import net.rocketeer.sevens.player.PlayerDatabase;
import net.rocketeer.sevens.player.SevensPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class KillLoggingListener implements Listener {
  private final PlayerDatabase database;
  private final JavaPlugin plugin;
  private final Set<String> trackedWorlds;

  public KillLoggingListener(JavaPlugin plugin, PlayerDatabase database, Set<String> trackedWorlds) {
    this.database = database;
    this.plugin = plugin;
    this.trackedWorlds = trackedWorlds;
  }

  private void logKill(PlayerDeathEvent event) {
    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
      try {
        Player killerPlayer = event.getEntity().getKiller();
        SevensPlayer target = this.database.findPlayer(event.getEntity().getUniqueId(), true);
        if (killerPlayer == null) {
          target.addDeathAgainst(target);
          return;
        }
        SevensPlayer killer = this.database.findPlayer(killerPlayer.getUniqueId(), true);
        killer.addKillAgainst(target);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  @EventHandler
  public void onDeathEvent(PlayerDeathEvent event) throws Exception {
    if (!this.trackedWorlds.contains(event.getEntity().getWorld().getName()))
      return;
    this.logKill(event);
  }
}

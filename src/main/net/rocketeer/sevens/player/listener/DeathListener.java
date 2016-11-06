package net.rocketeer.sevens.player.listener;

import net.rocketeer.sevens.player.PlayerDatabase;
import net.rocketeer.sevens.player.SevensPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {
  private final PlayerDatabase database;

  public DeathListener(PlayerDatabase database) {
    this.database = database;
  }

  @EventHandler
  public void onDeathEvent(PlayerDeathEvent event) throws Exception {
    if (!event.getEntity().getWorld().getName().equals("bendarenas"))
      return;
    SevensPlayer killer = this.database.findPlayer(event.getEntity().getKiller().getUniqueId());
    SevensPlayer target = this.database.findPlayer(event.getEntity().getUniqueId());
    killer.addKillAgainst(target);
  }
}

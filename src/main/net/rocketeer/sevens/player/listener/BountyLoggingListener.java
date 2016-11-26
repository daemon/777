package net.rocketeer.sevens.player.listener;

import net.rocketeer.sevens.game.bounty.BountyRegistry;
import net.rocketeer.sevens.game.bounty.BountyRewardEvent;
import net.rocketeer.sevens.player.PlayerDatabase;
import net.rocketeer.sevens.player.SevensPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BountyLoggingListener implements Listener {
  private final PlayerDatabase database;
  private final BountyRegistry registry;
  private final JavaPlugin plugin;

  public BountyLoggingListener(JavaPlugin plugin, PlayerDatabase database, BountyRegistry registry) {
    this.database = database;
    this.registry = registry;
    this.plugin = plugin;
  }

  @EventHandler
  public void onBountyRewardEvent(BountyRewardEvent event) {
    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
      try {
        SevensPlayer player = this.database.findPlayer(event.player().getUniqueId());
        player.addScore(event.bountyReward());
        Bukkit.getScheduler().runTask(this.plugin, () -> {
          event.player().sendMessage("You've earned " + ChatColor.AQUA + event.bountyReward() + ChatColor.WHITE + " points");
        });
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}

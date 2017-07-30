package net.rocketeer.sevens.player;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class SyncServerMessage {
  private final String message;
  private final JavaPlugin plugin;

  public SyncServerMessage(JavaPlugin plugin, String message) {
    this.plugin = plugin;
    this.message = message;
  }

  public void send() {
    Bukkit.getScheduler().runTask(this.plugin, () -> {
      Bukkit.getServer().broadcastMessage(this.message);
    });
  }
}

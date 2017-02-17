package net.rocketeer.sevens.player;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SyncPlayerMessage {
  private final String message;
  private final JavaPlugin plugin;
  private final CommandSender sender;

  public SyncPlayerMessage(JavaPlugin plugin, CommandSender sender, String message) {
    this.plugin = plugin;
    this.sender = sender;
    this.message = message;
  }

  public void send() {
    Bukkit.getScheduler().runTask(this.plugin, () -> {
      this.sender.sendMessage(this.message);
    });
  }
}

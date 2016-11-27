package net.rocketeer.sevens.command;

import net.rocketeer.sevens.player.PlayerDatabase;
import net.rocketeer.sevens.player.SevensPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ScoreCommand implements CommandExecutor {
  private final PlayerDatabase database;
  private final JavaPlugin plugin;

  public ScoreCommand(JavaPlugin plugin, PlayerDatabase database) {
    this.database = database;
    this.plugin = plugin;
  }

  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    if (args.length > 0) {
      String lookupPlayerName = args[0];
      OfflinePlayer lookupPlayer = Bukkit.getOfflinePlayer(lookupPlayerName);
      if (lookupPlayer == null) {
        sender.sendMessage(ChatColor.RED + "Player not found!");
        return true;
      }
    }
    if (!(sender instanceof Player))
      return true;
    Player player = (Player) sender;
    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
      try {
        SevensPlayer sPlayer = this.database.findPlayer(player.getUniqueId());
        Bukkit.getScheduler().runTask(this.plugin, () -> player.sendMessage("Score: " + ChatColor.AQUA + sPlayer.score()));
      } catch (Exception e) {
        e.printStackTrace();
        Bukkit.getScheduler().runTask(this.plugin, () -> player.sendMessage(ChatColor.RED + "Error retrieving score!"));
      }
    });
    return true;
  }
}

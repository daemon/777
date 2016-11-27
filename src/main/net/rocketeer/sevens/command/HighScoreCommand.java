package net.rocketeer.sevens.command;

import net.rocketeer.sevens.player.PlayerDatabase;
import net.rocketeer.sevens.player.SevensPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.StringJoiner;

public class HighScoreCommand implements CommandExecutor {
  private final PlayerDatabase database;
  private final JavaPlugin plugin;

  public HighScoreCommand(JavaPlugin plugin, PlayerDatabase database) {
    this.database = database;
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    int page = 0;
    if (args.length > 0)
      try {
        page = Integer.parseInt(args[0]) - 1;
      } catch (Exception ignored) {}
    if (page < 0) {
      sender.sendMessage(ChatColor.RED + "Page number must be non-negative!");
      return false;
    }
    final int finalPage = page;
    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
      try {
        List<SevensPlayer> players = this.database.fetchTopPlayers(finalPage * 10, 10);
        StringJoiner joiner = new StringJoiner("\n");
        if (players.size() == 0) {
          Bukkit.getScheduler().runTask(this.plugin, () -> sender.sendMessage(ChatColor.RED + "No records at that page"));
          return;
        }
        final String fmtStr = "%d. " + ChatColor.AQUA + "%s " + ChatColor.GOLD + "%d" + ChatColor.WHITE;
        for (int i = 0; i < players.size(); ++i) {
          SevensPlayer player = players.get(i);
          OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.uuid());
          if (offlinePlayer == null)
            continue;
          joiner.add(String.format(fmtStr, finalPage * 10 + i + 1, offlinePlayer.getName(), player.score()));
        }
        Bukkit.getScheduler().runTask(this.plugin, () -> sender.sendMessage(joiner.toString()));
      } catch (Exception e) {
        e.printStackTrace();
        Bukkit.getScheduler().runTask(this.plugin, () -> sender.sendMessage(ChatColor.RED + "Error fetching high score list"));
      }
    });
    return true;
  }
}

package net.rocketeer.sevens.command;

import net.rocketeer.sevens.player.PlayerDatabase;
import net.rocketeer.sevens.player.PlayerRank;
import net.rocketeer.sevens.player.SevensPlayer;
import net.rocketeer.sevens.player.SyncPlayerMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class RankCommand implements CommandExecutor {
  private final PlayerDatabase database;
  private final JavaPlugin plugin;

  public RankCommand(JavaPlugin plugin, PlayerDatabase database) {
    this.database = database;
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    String name = args.length > 0 ? args[0] : sender.getName();
    OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(name);
    if (player == null || !player.hasPlayedBefore()) {
      sender.sendMessage(ChatColor.RED + "Player not found!");
      return true;
    }
    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
      try {
        SevensPlayer sp = this.database.findPlayer(player.getUniqueId(), false);
        PlayerRank rank = this.database.computeRank(sp);
        new SyncPlayerMessage(this.plugin, sender, String.format("%s's rank: %s%s", player.getName(), ChatColor.GOLD.toString(), rank.name)).send();
      } catch (Exception e) {}
    });
    return true;
  }
}

package net.rocketeer.sevens.command;

import net.rocketeer.sevens.game.name.StaticTagManager;
import net.rocketeer.sevens.player.PlayerDatabase;
import net.rocketeer.sevens.player.SevensPlayer;
import net.rocketeer.sevens.player.SyncPlayerMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class ScoreCommand implements CommandExecutor {
  private final PlayerDatabase database;
  private final JavaPlugin plugin;

  public ScoreCommand(JavaPlugin plugin, PlayerDatabase database) {
    this.database = database;
    this.plugin = plugin;
  }

  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    OfflinePlayer lookupPlayer = null;
    if (args.length > 0) {
      String lookupPlayerName = args[0];
      lookupPlayer = Bukkit.getOfflinePlayer(lookupPlayerName);
      if (lookupPlayer == null) {
        sender.sendMessage(ChatColor.RED + "Player not found!");
        return true;
      }
    }

    if (sender instanceof Player && lookupPlayer == null)
      lookupPlayer = (OfflinePlayer) sender;
    else if (lookupPlayer == null)
      return true;
    final OfflinePlayer finalLookupPlayer = lookupPlayer;
    final UUID uuid = finalLookupPlayer.getUniqueId();
    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
      try {
        SevensPlayer sPlayer = this.database.findPlayer(uuid, false);
        if (sPlayer == null) {
          new SyncPlayerMessage(this.plugin, sender, ChatColor.RED + "Player not found!").send();
          return;
        }
        String message = "Total score: " + ChatColor.GOLD + sPlayer.score() + ChatColor.WHITE + " points";
        new SyncPlayerMessage(this.plugin, sender, message).send();
      } catch (Exception e) {
        e.printStackTrace();
        new SyncPlayerMessage(this.plugin, sender, ChatColor.RED + "Error retrieving score!").send();
      }
    });
    return true;
  }
}

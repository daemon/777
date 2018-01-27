package net.rocketeer.sevens.command;

import net.rocketeer.sevens.player.PlayerDatabase;
import net.rocketeer.sevens.player.SevensPlayer;
import net.rocketeer.sevens.player.SyncPlayerMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ToggleEnabledCommand implements CommandExecutor {
  private final PlayerDatabase database;
  private final JavaPlugin plugin;

  public ToggleEnabledCommand(JavaPlugin plugin, PlayerDatabase database) {
    this.plugin = plugin;
    this.database = database;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    if (!(sender instanceof Player))
      return true;
    Player player = (Player) sender;
    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
      try {
        SevensPlayer sPlayer = this.database.findPlayer(player.getUniqueId(), true);
        this.database.updateEnabled(sPlayer, !sPlayer.isEnabled());
        String status = sPlayer.isEnabled() ? ChatColor.RED + "disabled" : ChatColor.AQUA + "enabled";
        String message = ChatColor.GOLD + "Rating system has now been " + status + ChatColor.GOLD + " for you.";
        new SyncPlayerMessage(this.plugin, player, message).send();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    return true;
  }
}

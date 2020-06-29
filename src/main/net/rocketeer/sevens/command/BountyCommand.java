package net.rocketeer.sevens.command;

import net.rocketeer.sevens.game.bounty.BountyRegistry;
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

import java.util.UUID;

public class BountyCommand implements CommandExecutor {
  private final BountyRegistry registry;
  private final JavaPlugin plugin;
  private final PlayerDatabase database;

  public BountyCommand(JavaPlugin plugin, PlayerDatabase database, BountyRegistry registry) {
    this.registry = registry;
    this.plugin = plugin;
    this.database = database;
  }

  private void addBounty(Player sender, Player target, int bounty) {
    if (bounty < 1) {
      sender.sendMessage(ChatColor.RED + "Bounty cannot be nonpositive!");
      return;
    }
    UUID uuid = sender.getUniqueId();
    if (this.registry.getAttribute(target) == null) {
      sender.sendMessage(ChatColor.RED + "Cannot find target player!");
      return;
    }
    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
      try {
        SevensPlayer sPlayer = this.database.findPlayer(uuid, false);
        synchronized (this) {
          if (sPlayer.score() < bounty) {
            new SyncPlayerMessage(this.plugin, sender, ChatColor.RED + "You need " + (bounty - sPlayer.score()) + " more points!").send();
            return;
          }
          this.database.updateScore(sPlayer, -bounty);
          Bukkit.getScheduler().runTask(this.plugin, () -> {
            int targetBounty = this.registry.getAttribute(target);
            this.registry.setAttribute(target, targetBounty + bounty);
            sender.sendMessage("Added bounty to " + ChatColor.AQUA + target.getName());
            Bukkit.broadcastMessage(ChatColor.AQUA + sender.getName() + ChatColor.GRAY + " added " + ChatColor.GOLD + bounty + ChatColor.GRAY + " points to " + ChatColor.AQUA + target.getName() + ChatColor.GRAY + "'s bounty!");
          });
        }
      } catch (Exception e) {
        new SyncPlayerMessage(this.plugin, sender, ChatColor.RED + "Database error!").send();
        e.printStackTrace();
      }
    });
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    if (!(sender instanceof Player))
      return true;
    if (args.length >= 3) {
      String cmd = args[0];
      if (!cmd.equalsIgnoreCase("add"))
        return false;
      String name = args[1];
      int bounty;
      try {
        bounty = Integer.parseInt(args[2]);
      } catch (Exception e) {
        return false;
      }
      Player player = Bukkit.getPlayer(name);
      if (player == null) {
        sender.sendMessage(ChatColor.RED + "Cannot find target player!");
        return true;
      }
      this.addBounty((Player) sender, player, bounty);
      return true;
    }
    Player player = (Player) sender;
    Integer bounty = this.registry.getAttribute(player);

    if (bounty == null) {
      player.sendMessage(ChatColor.RED + "You don't have a bounty!");
      return true;
    }

    player.sendMessage("Current bounty: " + ChatColor.GOLD + bounty + ChatColor.WHITE + " points");
    return true;
  }
}

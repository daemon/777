package net.rocketeer.sevens.command;

import net.rocketeer.sevens.game.spree.SpreeRegistry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpreeCommand implements CommandExecutor {
  private final SpreeRegistry registry;

  public SpreeCommand(SpreeRegistry registry) {
    this.registry = registry;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    if (!(sender instanceof Player))
      return true;
    Player player = (Player) sender;
    Integer spree = this.registry.getAttribute(player);
    if (spree == null) {
      player.sendMessage(ChatColor.RED + "You don't have a spree!");
      return true;
    }

    String suffix = " kill";
    if (spree != 1)
      suffix += "s";
    player.sendMessage("Current spree: " + ChatColor.GOLD + spree + ChatColor.WHITE + suffix);
    return true;
  }
}

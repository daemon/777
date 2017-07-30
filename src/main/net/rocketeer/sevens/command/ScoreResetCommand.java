package net.rocketeer.sevens.command;

import net.rocketeer.sevens.player.PlayerDatabase;
import net.rocketeer.sevens.player.SevensPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class ScoreResetCommand implements CommandExecutor {
  private final JavaPlugin plugin;
  private final PlayerDatabase database;

  public ScoreResetCommand(JavaPlugin plugin, PlayerDatabase database) {
    this.plugin = plugin;
    this.database = database;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
      try {
        List<SevensPlayer> players = this.database.fetchTopScorePlayers(0, 10);
        Bukkit.getScheduler().runTask(this.plugin, () -> {
          List<String> uuids = players.stream().map(p -> p.uuid().toString()).collect(Collectors.toList());
          this.plugin.getConfig().set("top", uuids);
          this.plugin.saveConfig();
        });
        this.database.resetAllScores();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    return true;
  }
}

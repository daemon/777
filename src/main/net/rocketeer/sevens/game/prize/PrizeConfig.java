package net.rocketeer.sevens.game.prize;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrizeConfig {
  private final Map<Integer, String> winnerTags;
  private final Map<Integer, String> winnerTitles;

  PrizeConfig(Map<Integer, String> winnerTags, Map<Integer, String> winnerTitles) {
    this.winnerTags = winnerTags;
    this.winnerTitles = winnerTitles;
  }

  public Map<Integer, String> winnerTags() {
    return this.winnerTags;
  }

  public Map<Integer, String> winnerTitles() {
    return this.winnerTitles;
  }

  public static PrizeConfig fromConfig(ConfigurationSection section) {
    List<String> tags = section.getStringList("winner-tags");
    List<String> titles = section.getStringList("winner-titles");
    Map<Integer, String> winnerTags = new HashMap<>();
    Map<Integer, String> winnerTitles = new HashMap<>();
    for (int i = 0; i < tags.size(); ++i)
      winnerTags.put(i, ChatColor.translateAlternateColorCodes('&', tags.get(i)));
    for (int i = 0; i < titles.size(); ++i)
      winnerTitles.put(i, ChatColor.translateAlternateColorCodes('&', titles.get(i)));
    return new PrizeConfig(winnerTags, winnerTitles);
  }
}

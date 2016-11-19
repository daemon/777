package net.rocketeer.sevens.game.spree;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpreeConfig {
  private final Map<Integer, String> spreeMessages;
  private final String spreeEndedMessage;
  private final int bountyIncrement;
  private final double bountyMultiplier;

  public SpreeConfig(Map<Integer, String> spreeMessages, String spreeEndedMessage, int bountyIncrement, double bountyMultiplier) {
    this.spreeMessages = spreeMessages;
    this.spreeEndedMessage = spreeEndedMessage;
    this.bountyIncrement = bountyIncrement;
    this.bountyMultiplier = bountyMultiplier;
  }

  public int bountyIncrement() {
     return this.bountyIncrement;
  }

  public double bountyMultiplier() {
    return this.bountyMultiplier;
  }

  public Map<Integer, String> spreeMessages() {
    return this.spreeMessages;
  }

  public String spreeEndedMessage() {
    return this.spreeEndedMessage;
  }

  public static String formatString(String fmtStr, String name, int spree) {
    return fmtStr.replace("{name}", name).replace("{spree}", String.valueOf(spree));
  }

  public static SpreeConfig fromConfig(ConfigurationSection config) {
    List<String> messages = config.getStringList("messages");
    Map<Integer, String> spreeMessages = new HashMap<>();
    for (String message : messages) {
      String[] tokens = message.split(">");
      int kills = Integer.parseInt(tokens[0]);
      String fmtStr = tokens[1];
      fmtStr = ChatColor.translateAlternateColorCodes('&', fmtStr);
      spreeMessages.put(kills, fmtStr);
    }
    String endedMessage = ChatColor.translateAlternateColorCodes('&', config.getString("ended-message"));
    int bountyIncrement = config.getInt("bounty-increment");
    double bountyMultiplier = config.getDouble("bounty-multiplier");
    return new SpreeConfig(spreeMessages, endedMessage, bountyIncrement, bountyMultiplier);
  }
}

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
  private final int maxMessageBounty;

  public SpreeConfig(Map<Integer, String> spreeMessages, String spreeEndedMessage, int bountyIncrement, int maxMessageBounty) {
    this.spreeMessages = spreeMessages;
    this.spreeEndedMessage = spreeEndedMessage;
    this.bountyIncrement = bountyIncrement;
    this.maxMessageBounty = maxMessageBounty;
  }

  public int bountyIncrement() {
     return this.bountyIncrement;
  }

  public Map<Integer, String> spreeMessages() {
    return this.spreeMessages;
  }

  public String spreeEndedMessage() {
    return this.spreeEndedMessage;
  }

  public int maxMessageBounty() {
    return this.maxMessageBounty;
  }

  public static String formatString(String fmtStr, String name, int spree) {
    return fmtStr.replace("{name}", name).replace("{spree}", String.valueOf(spree));
  }

  public static SpreeConfig fromConfig(ConfigurationSection config) {
    List<String> messages = config.getStringList("messages");
    Map<Integer, String> spreeMessages = new HashMap<>();
    int maxMessageBounty = 0;
    for (String message : messages) {
      String[] tokens = message.split(">");
      int kills = Integer.parseInt(tokens[0]);
      String fmtStr = tokens[1];
      fmtStr = ChatColor.translateAlternateColorCodes('&', fmtStr);
      spreeMessages.put(kills, fmtStr);
      if (kills > maxMessageBounty)
        maxMessageBounty = kills;
    }
    String endedMessage = ChatColor.translateAlternateColorCodes('&', config.getString("ended-message"));
    int bountyIncrement = config.getInt("bounty-increment");
    return new SpreeConfig(spreeMessages, endedMessage, bountyIncrement, maxMessageBounty);
  }
}

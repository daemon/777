package net.rocketeer.sevens.player;

import org.bukkit.ChatColor;

public enum PlayerRank {
  UNRANKED("Unranked", 0, 0), WOOD3("Wood III", 0.08, 1), WOOD2("Wood II", 0.16, 1), WOOD1("Wood I", 0.25, 1),
  IRON3("Iron III", 0.34, 2), IRON2("Iron II", 0.44, 2), IRON1("Iron I", 0.55, 2),
  GOLD3("Gold III", 0.65, 3), GOLD2("Gold II", 0.72, 3), GOLD1("Gold I", 0.8, 3),
  DIAMOND3("Diamond III", 0.85, 4), DIAMOND2("Diamond II", 0.9, 4), DIAMOND1("Diamond I", 0.95, 4), GOD("God", 1, 5);
  public final double percentile;
  public final String name;
  public final int tier;
  public final String category;

  private ChatColor[] colorMap = { ChatColor.GRAY, ChatColor.YELLOW, ChatColor.WHITE, ChatColor.GOLD, ChatColor.AQUA, ChatColor.LIGHT_PURPLE };

  PlayerRank(String name, double percentile, int tier) {
    this.name = name;
    this.percentile = percentile;
    this.tier = tier;
    this.category = name.split(" ")[0];
  }

  public ChatColor color() {
    return this.colorMap[this.tier];
  }

  public static PlayerRank percentileToRank(double percentile) {
    for (PlayerRank rank : PlayerRank.values())
      if (percentile <= rank.percentile)
        return rank;
    throw new IllegalArgumentException("percentile must be less than or equal to 1");
  }
}

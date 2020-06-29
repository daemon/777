package net.rocketeer.sevens.player;

import org.bukkit.ChatColor;

public enum PlayerRank {
  UNRANKED("Unranked", -100, 0),
  BRONZE3("Bronze III", 1.32, 1),
  BRONZE2("Bronze II", 4.5, 1),
  BRONZE1("Bronze I", 7, 1),
  IRON3("Silver III", 9, 2),
  IRON2("Silver II", 11.45, 2),
  IRON1("Silver I", 14, 2),
  GOLD3("Gold III", 16, 3),
  GOLD2("Gold II", 18, 3),
  GOLD1("Gold I", 19.67, 3),
  DIAMOND3("Diamond III", 21, 4),
  DIAMOND2("Diamond II", 23, 4),
  DIAMOND1("Diamond I", 25, 4),
  GOD("God", 100, 5);
  public final double rating;
  public final String name;
  public final int tier;
  public final String category;

  private ChatColor[] colorMap = { ChatColor.GRAY, ChatColor.YELLOW, ChatColor.WHITE, ChatColor.GOLD, ChatColor.AQUA, ChatColor.LIGHT_PURPLE };

  PlayerRank(String name, double rating, int tier) {
    this.name = name;
    this.rating = rating;
    this.tier = tier;
    this.category = name.split(" ")[0];
  }

  public ChatColor color() {
    return this.colorMap[this.tier];
  }

  public static PlayerRank ratingToRank(double rating) {
    for (PlayerRank rank : PlayerRank.values())
      if (rating <= rank.rating)
        return rank;
    return UNRANKED;
  }
}

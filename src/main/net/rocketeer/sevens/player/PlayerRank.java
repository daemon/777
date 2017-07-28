package net.rocketeer.sevens.player;

public enum PlayerRank {
  WOOD3("Wood III", 0.05), WOOD2("Wood II", 0.15), WOOD1("Wood I", 0.25),
  IRON3("Iron III", 0.33), IRON2("Iron II", 0.41), IRON1("Iron I", 0.5),
  GOLD3("Gold III", 0.58), GOLD2("Gold II", 0.66), GOLD1("Gold I", 0.75),
  DIAMOND3("Diamond III", 0.82), DIAMOND2("Diamond II", 0.89), DIAMOND1("Diamond I", 0.95), GOD("God", 1);
  public final double percentile;
  public final String name;

  PlayerRank(String name, double percentile) {
    this.name = name;
    this.percentile = percentile;
  }

  public static PlayerRank percentileToRank(double percentile) {
    for (PlayerRank rank : PlayerRank.values())
      if (percentile < rank.percentile)
        return rank;
    throw new IllegalArgumentException("percentile must be less than or equal to 1");
  }
}

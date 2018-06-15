package net.rocketeer.sevens.player.listener;

import net.rocketeer.sevens.player.PlayerDatabase;
import net.rocketeer.sevens.player.PlayerRank;
import net.rocketeer.sevens.player.SevensPlayer;
import net.rocketeer.sevens.player.SyncServerMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.goochjs.jskills.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KillRatingListener implements Listener {
  private final PlayerDatabase database;
  private final JavaPlugin plugin;
  private final Set<String> trackedWorlds;
  private final Map<Player, List<RewardData>> rewardMap = new HashMap<>();
  private final Map<UUID, RankData> lastRankMap = new ConcurrentHashMap<>();
  private final Map<UUID, String> nameCache = new ConcurrentHashMap<>();

  public KillRatingListener(JavaPlugin plugin, PlayerDatabase database, Set<String> trackedWorlds) {
    this.database = database;
    this.plugin = plugin;
    this.trackedWorlds = trackedWorlds;
    Bukkit.getScheduler().runTaskTimer(this.plugin, new ExpireMapTask(), 0, 12000);
  }

  private void computeNewRatings(Player losingPlayer, List<RewardData> rdList) throws Exception {
    SevensPlayer loser = this.database.findPlayer(losingPlayer.getUniqueId(), true);
    if (loser == null || !loser.isEnabled())
      return;
    Team losingTeam = new Team(new org.goochjs.jskills.Player<>(loser), new Rating(loser.mu(), loser.sigma()));
    Team winningTeam = new Team();
    List<ITeam> teams = Arrays.asList(winningTeam, losingTeam);
    Map<UUID, Double> contribMap = new HashMap<>();
    double total = 0;
    for (RewardData rd : rdList) {
      total += rd.damage;
      contribMap.putIfAbsent(rd.causeEntityUuid, 0.0);
      contribMap.put(rd.causeEntityUuid, contribMap.get(rd.causeEntityUuid) + rd.damage);
    }
    double finalTotal = total;
    contribMap.forEach((uuid, contrib) -> {
      if (uuid.equals(losingPlayer.getUniqueId()))
        return;
      SevensPlayer p;
      try {
        p = this.database.findPlayer(uuid, false);
        if (p == null || !p.isEnabled())
          return;
      } catch (Exception ignored) {
        return;
      }
      winningTeam.addPlayer(new org.goochjs.jskills.Player<>(p, contrib / finalTotal), new Rating(p.mu(), p.sigma()));
    });
    if (winningTeam.isEmpty())
      return;
    Map<IPlayer, Rating> ratings = TrueSkillCalculator.calculateNewRatings(GameInfo.getDefaultGameInfo(), teams, 1, 2);
    ratings.forEach((p, r) -> {
      SevensPlayer player = ((org.goochjs.jskills.Player<SevensPlayer>) p).getId();
      try {
        player.addRating(r.getMean() - player.mu(), r.getStandardDeviation() - player.sigma());
        PlayerRank rank = this.database.computeRank(player);
        RankData lastRankData = this.lastRankMap.get(player.uuid());
        String name = this.cachedName(player.uuid());
        if (lastRankData != null && lastRankData.rank.tier != rank.tier && lastRankData.rating != player.rating() && name != null) {
          String message = ChatColor.AQUA + "%s " + ChatColor.WHITE + "is now " + rank.color() + rank.category;
          message = String.format(message, name);
          new SyncServerMessage(this.plugin, message).send();
        }
        this.lastRankMap.put(player.uuid(), new RankData(player.rating(), rank));
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  private String cachedName(UUID uuid) {
    return this.nameCache.get(uuid);
  }

  private void logKill(PlayerDeathEvent event) throws InterruptedException {
    List<RewardData> rdList = this.rewardMap.get(event.getEntity());
    if (rdList == null)
      return;
    this.rewardMap.remove(event.getEntity());
    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
      try {
        this.computeNewRatings(event.getEntity(), rdList);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  @EventHandler
  public void onDeathEvent(PlayerDeathEvent event) throws Exception {
    if (!this.trackedWorlds.contains(event.getEntity().getWorld().getName()))
      return;
    this.logKill(event);
  }

  @EventHandler(ignoreCancelled = true)
  public void onDamageEvent(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
      return;
    if (!this.trackedWorlds.contains(event.getEntity().getWorld().getName()))
      return;
    Player player = (Player) event.getEntity();
    this.rewardMap.putIfAbsent(player, new LinkedList<>());
    this.nameCache.putIfAbsent(player.getUniqueId(), player.getName());
    List<RewardData> rewardDataList = this.rewardMap.get(player);
    if (rewardDataList.size() > 0 && rewardDataList.get(rewardDataList.size() - 1).causeEntityUuid.equals(event.getDamager().getUniqueId())) {
      RewardData last = rewardDataList.get(rewardDataList.size() - 1);
      last.damage += event.getDamage();
      last.renewTimestamp();
    } else {
      rewardDataList.add(new RewardData(event.getDamager().getUniqueId(), event.getDamage()));
    }
  }

  @EventHandler
  public void onHealEvent(EntityRegainHealthEvent event) {
    if (!(event.getEntity() instanceof Player))
      return;
    if (!this.trackedWorlds.contains(event.getEntity().getWorld().getName()))
      return;
    Player player = (Player) event.getEntity();
    if (!this.rewardMap.containsKey(player))
      return;
    List<RewardData> rewardDataList = this.rewardMap.get(player);
    double healAmount = event.getAmount();
    while (healAmount > 0 && rewardDataList.size() > 0) {
      RewardData first = rewardDataList.get(0);
      first.damage -= healAmount;
      healAmount = -Math.min(0, first.damage);
      if (first.damage <= 0)
        rewardDataList.remove(0);
    }
  }

  private static class RewardData {
    private long timestamp;
    private final UUID causeEntityUuid;
    private double damage;

    private RewardData(UUID causeEntityUuid, double damage) {
      this.causeEntityUuid = causeEntityUuid;
      this.damage = damage;
      this.timestamp = System.currentTimeMillis();
    }

    private void renewTimestamp() {
      this.timestamp = System.currentTimeMillis();
    }
  }

  private static class RankData {
    private double rating;
    private PlayerRank rank;

    private RankData(double rating, PlayerRank rank) {
      this.rank = rank;
      this.rating = rating;
    }
  }

  private class ExpireMapTask implements Runnable {
    @Override
    public void run() {
      rewardMap.forEach((p, list) -> list.removeIf(r -> r.timestamp < System.currentTimeMillis() - 60000));
      rewardMap.entrySet().removeIf(e -> e.getValue().isEmpty());
      nameCache.clear();
    }
  }
}

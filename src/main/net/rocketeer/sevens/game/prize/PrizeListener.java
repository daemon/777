package net.rocketeer.sevens.game.prize;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.rocketeer.sevens.game.name.NameTagChangeEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PrizeListener implements Listener {
  private final PrizeConfig config;
  private Map<UUID, Integer> uuidsMap = new HashMap<>();
  public PrizeListener(JavaPlugin plugin, PrizeConfig config, List<UUID> topUuids) {
    for (int i = 0; i < topUuids.size(); ++i)
      this.uuidsMap.put(topUuids.get(i), i);
    this.config = config;
  }

  public void onChatMessage(PlayerChatEvent event) {
    /*Player player = event.getPlayer();
    if (!this.uuidsMap.containsKey(event.getPlayer().getUniqueId()))
      return;
    int rank = this.uuidsMap.get(event.getPlayer().getUniqueId());
    if (!this.config.winnerTitles().containsKey(rank))
      return;
    String title = this.config.winnerTitles().get(rank);*/
    // event.setMessage(title + ChatColor.WHITE + event.getMessage());
  }

  @EventHandler(priority=EventPriority.HIGHEST)
  public void onNameTagChange(NameTagChangeEvent event) {
    Player player = event.tag().owner();
    if (!this.uuidsMap.containsKey(player.getUniqueId()))
      return;
    int rank = this.uuidsMap.get(player.getUniqueId());
    if (!this.config.winnerTags().containsKey(rank))
      return;
    String tag = this.config.winnerTags().get(rank).replace('?', '\u2744');
    event.tag().setTag(tag + " " + ChatColor.WHITE + event.tag().toString() + " " + tag);
  }
}

package net.rocketeer.sevens.game.name;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.rocketeer.sevens.net.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class NameTag {
  private static int ServerVersion = 9;

  private final Player owner;
  private String tag;
  private static int idCounter = 133294;
  private final int id;
  private final Set<Player> shownPlayers = new HashSet<>();
  private WrappedDataWatcher watcher = new WrappedDataWatcher();
  private WrappedDataWatcher.Serializer ss;
  private WrappedDataWatcher.Serializer ocs;
  private boolean inCall = false;

  static {
    try {
      ServerVersion = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().substring(23).split("_")[1]);
    } catch (Exception e) {
      ServerVersion = 9;
    }
  }

  NameTag(Player owner, String tag) {
    this.owner = owner;
    this.tag = tag;
    if (idCounter == Integer.MAX_VALUE)
      idCounter = 133294;
    this.id = ++idCounter;
    this.initWatcher();
    Bukkit.getPluginManager().callEvent(new NameTagChangeEvent(this));
  }

  @Override
  public String toString() {
    return this.tag;
  }

  private void initWatcher() {
    this.ss = WrappedDataWatcher.Registry.get(String.class);
    this.ocs = WrappedDataWatcher.Registry.getChatComponentSerializer(true);

    WrappedDataWatcher.Serializer bs = WrappedDataWatcher.Registry.get(Byte.class);
    WrappedDataWatcher.Serializer bls = WrappedDataWatcher.Registry.get(Boolean.class);
    this.watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, bs), (byte) 0x20);
    setMetadataTag();
    this.watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, bls), true);
    byte byteData = 0x01 | 0x08 | 0x10;
    int maskIndex = (ServerVersion > 9) ? 11 : 10;
    this.watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(maskIndex, bs), byteData);
  }

  public boolean isVisibleTo(Player other) {
    return this.shownPlayers.contains(other);
  }

  public Set<Player> visiblePlayers() {
    return this.shownPlayers;
  }

  public void setTag(String tag) {
    this.tag = tag;
    this.visiblePlayers().forEach(player -> {
      setMetadataTag();
      WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
      metadata.setEntityID(this.id);
      metadata.setMetadata(watcher.getWatchableObjects());
      metadata.sendPacket(player);
    });
    if (this.inCall)
      return;
    this.inCall = true;
    try {
      Bukkit.getPluginManager().callEvent(new NameTagChangeEvent(this));
    } finally {
      this.inCall = false;
    }
  }
  
  private void setMetadataTag() {
    if (ServerVersion < 13) {
      watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, this.ss), this.tag);
    } else {
      WrappedChatComponent chatTag = WrappedChatComponent.fromText(this.tag);
      watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, this.ocs), Optional.of(chatTag.getHandle()));
    }
  }

  public Player owner() {
    return this.owner;
  }

  public void update(Player player) {
    WrapperPlayServerEntityTeleport tpPacket = new WrapperPlayServerEntityTeleport();
    tpPacket.setEntityID(this.id);
    tpPacket.setX(this.owner.getLocation().getX());
    tpPacket.setY(this.owner.getLocation().getY() + 2.05);
    tpPacket.setZ(this.owner.getLocation().getZ());
    tpPacket.sendPacket(player);
    WrapperPlayServerEntityVelocity vPacket = new WrapperPlayServerEntityVelocity();
    vPacket.setEntityID(this.id);
    vPacket.setVelocityX(this.owner.getVelocity().getX());
    vPacket.setVelocityY(this.owner.getVelocity().getY());
    vPacket.setVelocityZ(this.owner.getVelocity().getZ());
    vPacket.sendPacket(player);
  }

  public void destroy() {
    this.shownPlayers.forEach(this::sendDestroy);
    this.shownPlayers.clear();
  }

  private void sendDestroy(Player player) {
    WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
    destroy.setEntities(new int[] {this.id});
    destroy.sendPacket(player);
  }

  public void destroy(Player player) {
    this.shownPlayers.remove(player);
    this.sendDestroy(player);
  }

  public void create(Player player) {
    if (!player.getWorld().equals(this.owner.getWorld()) || player.equals(this.owner))
      return;
    this.shownPlayers.add(player);
    WrapperPlayServerSpawnEntity wrapper = new WrapperPlayServerSpawnEntity();
    wrapper.setEntityID(this.id);
    wrapper.setType(WrapperPlayServerSpawnEntity.ObjectTypes.ARMORSTAND);
    wrapper.setX(this.owner.getLocation().getX());
    wrapper.setY(this.owner.getLocation().getY() + 2.05);
    wrapper.setZ(this.owner.getLocation().getZ());
    wrapper.setUniqueId(UUID.randomUUID());
    wrapper.sendPacket(player);
    WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
    metadata.setEntityID(this.id);
    metadata.setMetadata(watcher.getWatchableObjects());
    metadata.sendPacket(player);
    this.update(player);
  }
}

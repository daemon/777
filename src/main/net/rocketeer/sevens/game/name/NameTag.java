package net.rocketeer.sevens.game.name;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.rocketeer.sevens.net.*;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NameTag {
  private final Player owner;
  private String tag;
  private static int idCounter = 133294;
  private final int id;
  private final Set<Player> shownPlayers = new HashSet<>();

  NameTag(Player owner, String tag) {
    this.owner = owner;
    this.tag = tag;
    if (idCounter == Integer.MAX_VALUE)
      idCounter = 133294;
    this.id = ++idCounter;
  }

  public boolean isVisibleTo(Player other) {
    return this.shownPlayers.contains(other);
  }

  public Set<Player> visiblePlayers() {
    return this.shownPlayers;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public Player owner() {
    return this.owner;
  }

  public void update(Player player) {
    WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
    metadata.setEntityID(this.id);
    WrappedDataWatcher.Serializer ss = WrappedDataWatcher.Registry.get(String.class);
    WrappedDataWatcher.Serializer bs = WrappedDataWatcher.Registry.get(Byte.class);
    WrappedDataWatcher.Serializer bls = WrappedDataWatcher.Registry.get(Boolean.class);
    WrappedDataWatcher watcher = new WrappedDataWatcher();
    watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, bs), (byte) 0x20);
    watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, ss), this.tag);
    watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, bls), true);
    WrappedDataWatcher.Serializer fs = WrappedDataWatcher.Registry.get(Float.class);
    // watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(9, fs), (float) 10.0);
    byte byteData = 0x01 | 0x08 | 0x10;
    watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(10, bs), byteData);
    metadata.setMetadata(watcher.getWatchableObjects());
    metadata.sendPacket(player);
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
    this.update(player);
  }
}

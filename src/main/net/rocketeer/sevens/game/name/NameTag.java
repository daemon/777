package net.rocketeer.sevens.game.name;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.rocketeer.sevens.net.*;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NameTag {
  private final Player owner;
  private String tag;
  private static int idCounter = 133294;
  private final int id;

  NameTag(Player owner, String tag) {
    this.owner = owner;
    this.tag = tag;
    this.id = ++idCounter;
  }

  public void setTag(String tag) {
    this.tag = tag;
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
    metadata.setMetadata(watcher.getWatchableObjects());
    metadata.sendPacket(player);
    WrapperPlayServerEntityTeleport tpPacket = new WrapperPlayServerEntityTeleport();
    tpPacket.setEntityID(this.id);
    tpPacket.setX(this.owner.getLocation().getX());
    tpPacket.setY(this.owner.getLocation().getY());
    tpPacket.setZ(this.owner.getLocation().getZ());
    tpPacket.sendPacket(player);
    WrapperPlayServerEntityVelocity vPacket = new WrapperPlayServerEntityVelocity();
    vPacket.setEntityID(this.id);
    vPacket.setVelocityX(this.owner.getVelocity().getX());
    vPacket.setVelocityY(this.owner.getVelocity().getY());
    vPacket.setVelocityZ(this.owner.getVelocity().getZ());
    vPacket.sendPacket(player);
  }

  public void destroy(Player player) {
    WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
    destroy.setEntities(new int[] {this.id});
    destroy.sendPacket(player);
  }

  public void destroy() {
    this.owner.getWorld().getPlayers().forEach(this::destroy);
  }

  public void create(Player player) {
    WrapperPlayServerSpawnEntity wrapper = new WrapperPlayServerSpawnEntity();
    wrapper.setEntityID(this.id);
    wrapper.setType(WrapperPlayServerSpawnEntity.ObjectTypes.ARMORSTAND);
    wrapper.setX(this.owner.getLocation().getX());
    wrapper.setY(this.owner.getLocation().getY());
    wrapper.setZ(this.owner.getLocation().getZ());
    wrapper.setUniqueId(UUID.randomUUID());
    wrapper.sendPacket(player);
    this.update(player);
  }

  public void create() {
    for (Player player : this.owner.getWorld().getPlayers()) {
      if (player.equals(this.owner))
        continue;
      this.create(player);
    }
  }

  public void update() {
    for (Player player : this.owner.getWorld().getPlayers()) {
      if (player.equals(this.owner))
        continue;
      this.update(player);
    }
  }
}

/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.rocketeer.sevens.net;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.PacketConstructor;
import com.comphenix.protocol.reflect.IntEnum;

public class WrapperPlayServerSpawnEntity extends AbstractPacket {
  public static final PacketType TYPE = PacketType.Play.Server.SPAWN_ENTITY_LIVING;

  private static PacketConstructor entityConstructor;

  /**
   * Represents the different object types.
   *
   * @author Kristian
   */
  public static class ObjectTypes extends IntEnum {
    public static final int ARMORSTAND = 1;

    /**
     * The singleton instance. Can also be retrieved from the parent class.
     */
    private static ObjectTypes INSTANCE = new ObjectTypes();

    /**
     * Retrieve an instance of the object types enum.
     *
     * @return Object type enum.
     */
    public static ObjectTypes getInstance() {
      return INSTANCE;
    }
  }

  public WrapperPlayServerSpawnEntity() {
    super(new PacketContainer(TYPE), TYPE);
    handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerSpawnEntity(PacketContainer packet) {
    super(packet, TYPE);
  }

  public WrapperPlayServerSpawnEntity(Entity entity, int type, int objectData) {
    super(fromEntity(entity, type, objectData), TYPE);
  }

  // Useful constructor
  private static PacketContainer fromEntity(Entity entity, int type,
                                            int objectData) {
    if (entityConstructor == null)
      entityConstructor =
          ProtocolLibrary.getProtocolManager()
              .createPacketConstructor(TYPE, entity, type,
                  objectData);
    return entityConstructor.createPacket(entity, type, objectData);
  }

  /**
   * Retrieve entity ID of the Object.
   *
   * @return The current EID
   */
  public int getEntityID() {
    return handle.getIntegers().read(0);
  }

  /**
   * Retrieve the entity that will be spawned.
   *
   * @param world - the current world of the entity.
   * @return The spawned entity.
   */
  public Entity getEntity(World world) {
    return handle.getEntityModifier(world).read(0);
  }

  /**
   * Retrieve the entity that will be spawned.
   *
   * @param event - the packet event.
   * @return The spawned entity.
   */
  public Entity getEntity(PacketEvent event) {
    return getEntity(event.getPlayer().getWorld());
  }

  /**
   * Set entity ID of the Object.
   *
   * @param value - new value.
   */
  public void setEntityID(int value) {
    handle.getIntegers().write(0, value);
  }

  public UUID getUniqueId() {
    return handle.getUUIDs().read(0);
  }

  public void setUniqueId(UUID value) {
    handle.getUUIDs().write(0, value);
  }

  /**
   * Retrieve the type of object. See {@link ObjectTypes}
   *
   * @return The current Type
   */
  public int getType() {
    return handle.getIntegers().read(1);
  }

  /**
   * Set the type of object. See {@link ObjectTypes}.
   *
   * @param value - new value.
   */
  public void setType(int value) {
    handle.getIntegers().write(1, value);
  }

  /**
   * Retrieve the x position of the object.
   * <p>
   * Note that the coordinate is rounded off to the nearest 1/32 of a meter.
   *
   * @return The current X
   */
  public double getX() {
    return handle.getDoubles().read(0);
  }

  /**
   * Set the x position of the object.
   *
   * @param value - new value.
   */
  public void setX(double value) {
    handle.getDoubles().write(0, value);
  }

  /**
   * Retrieve the y position of the object.
   * <p>
   * Note that the coordinate is rounded off to the nearest 1/32 of a meter.
   *
   * @return The current y
   */
  public double getY() {
    return handle.getDoubles().read(1);
  }

  /**
   * Set the y position of the object.
   *
   * @param value - new value.
   */
  public void setY(double value) {
    handle.getDoubles().write(1, value);
  }

  /**
   * Retrieve the z position of the object.
   * <p>
   * Note that the coordinate is rounded off to the nearest 1/32 of a meter.
   *
   * @return The current z
   */
  public double getZ() {
    return handle.getDoubles().read(2);
  }

  /**
   * Set the z position of the object.
   *
   * @param value - new value.
   */
  public void setZ(double value) {
    handle.getDoubles().write(2, value);
  }

  /**
   * Retrieve the yaw.
   *
   * @return The current Yaw
   */
  public float getYaw() {
    return (handle.getBytes().read(0) * 360.F) / 256.0F;
  }

  /**
   * Set the yaw of the object spawned.
   *
   * @param value - new yaw.
   */
  public void setYaw(float value) {
    handle.getBytes().write(0, (byte) (value * 256.0F / 360.0F));
  }


  /**
   * Retrieve the pitch.
   *
   * @return The current pitch.
   */
  public float getPitch() {
    return (handle.getBytes().read(1) * 360.F) / 256.0F;
  }

  /**
   * Set the pitch.
   *
   * @param value - new pitch.
   */
  public void setPitch(float value) {
    handle.getBytes().write(1, (byte) (value * 256.0F / 360.0F));
  }

  /**
   * Retrieve the head pitch.
   *
   * @return The current head pitch.
   */
  public float getHeadPitch() {
    return (handle.getBytes().read(2) * 360.F) / 256.0F;
  }

  /**
   * Set the head pitch.
   *
   * @param value - new head pitch.
   */
  public void setHeadPitch(float value) {
    handle.getBytes().write(2, (byte) (value * 256.0F / 360.0F));
  }

  /**
   * Retrieve the optional speed x.
   * <p>
   *
   * @return The optional speed x.
   */
  public double getOptionalSpeedX() {
    return handle.getIntegers().read(2) / 8000.0D;
  }

  /**
   * Set the optional speed x.
   *
   * @param value - new value.
   */
  public void setOptionalSpeedX(double value) {
    handle.getIntegers().write(2, (int) (value * 8000.0D));
  }

  /**
   * Retrieve the optional speed y.
   * <p>
   *
   * @return The optional speed y.
   */
  public double getOptionalSpeedY() {
    return handle.getIntegers().read(3) / 8000.0D;
  }

  /**
   * Set the optional speed y.
   *
   * @param value - new value.
   */
  public void setOptionalSpeedY(double value) {
    handle.getIntegers().write(3, (int) (value * 8000.0D));
  }

  /**
   * Retrieve the optional speed z.
   * <p>
   *
   * @return The optional speed z.
   */
  public double getOptionalSpeedZ() {
    return handle.getIntegers().read(4) / 8000.0D;
  }

  /**
   * Set the optional speed z.
   *
   * @param value - new value.
   */
  public void setOptionalSpeedZ(double value) {
    handle.getIntegers().write(4, (int) (value * 8000.0D));
  }
}
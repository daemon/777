/*
 *  PacketWrapper - Contains wrappers for each packet in Minecraft.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU Lesser General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

package net.rocketeer.sevens.net;

import java.util.List;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.primitives.Ints;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class WrapperPlayServerEntityDestroy extends AbstractPacket {
  public static final PacketType TYPE = PacketType.Play.Server.ENTITY_DESTROY;

  public WrapperPlayServerEntityDestroy() {
    super(new PacketContainer(TYPE), TYPE);
    handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerEntityDestroy(PacketContainer packet) {
    super(packet, TYPE);
  }

  /**
   * Retrieve the IDs of the entities that will be destroyed.
   * @return The current entities.
   */
  public List<Integer> getEntities() {
    IntArrayList list = (IntArrayList)handle.getModifier().read(0);
    return Ints.asList(list.toIntArray());
  }

  /**
   * Set the entities that will be destroyed.
   */
  public void setEntities(int[] entities) {
    handle.getModifier().write(0, new IntArrayList(entities));
  }

  /**
   * Set the entities that will be destroyed.
   */
  public void setEntities(List<Integer> entities) {
    setEntities(Ints.toArray(entities));
  }
}
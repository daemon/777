package net.rocketeer.sevens.game.name;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.Arrays;

public class StaticTag {
  private String text;
  private final Location location;
  private ArmorStand armorStand;

  StaticTag(Location location, String text) {
    this.text = text;
    this.location = location;
  }

  StaticTag(Location location) {
    this.location = location;
    this.text = "";
  }

  public void spawn() {
    this.armorStand = this.location.getWorld().spawn(this.location, ArmorStand.class);
    this.armorStand.setVisible(false);
    this.armorStand.setCustomName(this.text);
    this.armorStand.setCustomNameVisible(true);
    this.armorStand.setBasePlate(false);
    this.armorStand.setSmall(true);
    this.armorStand.setGravity(false);
    this.armorStand.setMarker(true);
  }

  public String text() {
    return this.text;
  }

  public String setText(String text) {
    String oldText = this.text;
    this.armorStand.setCustomName(this.text);
    this.text = text;
    return oldText;
  }

  public void despawn() {
    armorStand.remove();
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof StaticTag))
      return false;
    StaticTag otherTag = (StaticTag) other;
    return otherTag.text.equals(this.text) && otherTag.location.equals(this.location);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new int[]{this.text.hashCode(), this.location.hashCode()});
  }
}

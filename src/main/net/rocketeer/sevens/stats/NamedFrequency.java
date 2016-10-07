package net.rocketeer.sevens.stats;

import org.apache.commons.math3.stat.Frequency;

public class NamedFrequency {
  public static final NamedFrequency EMPTY = new NamedFrequency("", new Frequency());
  private final String name;
  private final Frequency frequency;

  public NamedFrequency(String name, Frequency frequency) {
    this.name = name;
    this.frequency = frequency;
  }

  public Frequency frequency() {
    return this.frequency;
  }

  public String name() {
    return this.name;
  }
}

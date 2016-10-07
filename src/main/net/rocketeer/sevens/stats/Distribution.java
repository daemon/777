package net.rocketeer.sevens.stats;

import org.apache.commons.math3.stat.Frequency;

import java.util.Arrays;
import java.util.Collection;

public class Distribution {
  private final Collection<NamedFrequency> frequencies;

  private Distribution(Collection<NamedFrequency> frequencies) {
    this.frequencies = frequencies;
  }

  public static Distribution asDistribution(NamedFrequency ... frequencies) {
    return new Distribution(Arrays.asList(frequencies));
  }

  public static Distribution asDistribution(Collection<NamedFrequency> frequencies) {
    return new Distribution(frequencies);
  }

  public class Query {

  }
}

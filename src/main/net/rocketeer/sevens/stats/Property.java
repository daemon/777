package net.rocketeer.sevens.stats;

import org.apache.commons.math3.stat.Frequency;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Collection;

@Entity
@Table(name="sevens_property")
public class Property<T> {
  private final String name;
  private final T data;

  public Property(String name, T data) {
    this.name = name;
    this.data = data;
  }

  public T data() {
    return this.data;
  }

  public static <U extends Comparable> NamedFrequency add(Property<U> ... properties) {
    return Property.add(Arrays.asList(properties));
  }

  public static <U extends Comparable> NamedFrequency add(Collection<Property<U>> properties) {
    if (properties.isEmpty())
      return NamedFrequency.EMPTY;
    Frequency frequency = new Frequency();
    String name = null;
    for (Property<U> property : properties) {
      if (name == null)
        name = property.name;
      frequency.addValue(property.data());
    }

    return new NamedFrequency(name, frequency);
  }
}

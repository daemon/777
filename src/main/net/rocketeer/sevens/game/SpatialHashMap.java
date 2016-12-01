package net.rocketeer.sevens.game;

import java.util.*;

// Sacrifices accuracy for speed. Designed for small (< 128) radius lookups and precise to 32 blocks.
// The best use case is for spaces with high concentrations of data points in sparse, irregular clusters.
public class SpatialHashMap<ValueType> {
  private Map<RadiusKey, Map<ExactKey, ValueType>> store = new HashMap<>();
  private final int radius = 32;
  private int size = 0;

  public int size() {
    return this.size;
  }

  public void put(int x, int y, int z, ValueType value) {
    RadiusKey key = new RadiusKey(x, y, z, this.radius);
    Map<ExactKey, ValueType> map = this.store.get(key);
    if (map == null) {
      map = new HashMap<>();
      this.store.put(key, map);
    }

    if (map.get(new ExactKey(x, y, z)) == null)
      ++size;
    map.put(new ExactKey(x, y, z), value);
  }

  public ValueType get(int x, int y, int z) {
    Map<ExactKey, ValueType> values = this.store.get(new RadiusKey(x, y, z, this.radius));
    if (values == null)
      return null;
    return values.get(new ExactKey(x, y, z));
  }

  public void remove(int x, int y, int z) {
    Map<ExactKey, ValueType> bucket = this.store.get(new RadiusKey(x, y, z, this.radius));
    if (bucket == null)
      return;
    if (bucket.remove(new ExactKey(x, y, z)) != null)
      --size;
  }

  public Set<ValueType> getWithin(int x, int y, int z, int radius) {
    Set<ValueType> values = new HashSet<>();
    for (int i = x - radius; i <= x + radius; i += this.radius)
      for (int j = y - radius; j <= y + radius; j += this.radius)
        for (int k = z - radius; k <= z + radius; k += this.radius) {
          RadiusKey key = new RadiusKey(i, j, k, this.radius);
          Map<ExactKey, ValueType> localMap = this.store.get(key);
          if (localMap == null)
            continue;
          localMap.forEach((unused, value) -> values.add(value));
        }
    return values;
  }

  public void clear() {
    this.store.clear();
    this.size = 0;
  }

  private static class ExactKey {
    private final int x;
    private final int y;
    private final int z;

    ExactKey(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(new int[]{this.x, this.y, this.z});
    }

    @Override
    public boolean equals(Object other) {
      if (!(other instanceof ExactKey))
        return false;
      ExactKey o = (ExactKey) other;
      return this.x == o.x && this.y == o.y && this.z == o.z;
    }
  }

  private static class RadiusKey extends ExactKey {
    RadiusKey(int x, int y, int z, int radius) {
      super(x / radius, y / radius, z / radius);
    }
  }
}

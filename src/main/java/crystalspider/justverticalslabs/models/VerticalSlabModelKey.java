package crystalspider.justverticalslabs.models;

import net.minecraft.core.Direction;

public class VerticalSlabModelKey {
  private final Direction side;
  private final String stateKey;

  public VerticalSlabModelKey(Direction side, String stateKey) {
    this.side = side;
    this.stateKey = stateKey;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    VerticalSlabModelKey other = (VerticalSlabModelKey) obj;
    if (side != other.side) {
      return false;
    }
    if (stateKey == null) {
      if (other.stateKey != null) {
        return false;
      }
    } else if (!stateKey.equals(other.stateKey)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((side == null) ? 0 : side.hashCode());
    result = prime * result + ((stateKey == null) ? 0 : stateKey.hashCode());
    return result;
  }
}

package crystalspider.justverticalslabs.model;

import java.util.Objects;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class VerticalSlabModelKey {
  private final Direction side;
  private final BlockState state;

  public VerticalSlabModelKey(Direction side, BlockState stateKey) {
    this.side = side;
    this.state = stateKey;
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
    if (state == null) {
      if (other.state != null) {
        return false;
      }
    } else if (!state.equals(other.state)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash(side, state);
  }
}

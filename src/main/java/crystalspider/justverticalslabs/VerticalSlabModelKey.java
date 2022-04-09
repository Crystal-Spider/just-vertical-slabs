package crystalspider.justverticalslabs;

import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Objects;

import net.minecraft.core.Direction;

public class VerticalSlabModelKey {
  private final Direction side;
  private final VoxelShape shape;

  public VerticalSlabModelKey(Direction side, VoxelShape shape) {
    this.side = side;
    this.shape = shape;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || object.getClass() != getClass()) {
      return false;
    }
    VerticalSlabModelKey verticalSlabModelKey = (VerticalSlabModelKey) object;
    return side == verticalSlabModelKey.side && shape.bounds().equals(verticalSlabModelKey.shape.bounds());
  }

  @Override
  public int hashCode() {
    return Objects.hash(side, shape.bounds().hashCode());
  }
}

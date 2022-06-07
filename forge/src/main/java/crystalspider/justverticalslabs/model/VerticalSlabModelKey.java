package crystalspider.justverticalslabs.model;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Utility wrapper around the two properties to use as key for BakedQuads cache.
 */
public class VerticalSlabModelKey {
  /**
   * {@link Direction side} indicating to which culling {@link Direction face} the BakedQuads are associated to.
   * If null, no culling {@link Direction face} is associated and the BakedQuads will always be rendered.
   */
  private final @Nullable Direction side;
  /**
   * {@link BlockState} associated to the BakedQuads, indicating which block textures are taken from.
   */
  private final @Nonnull BlockState state;
  /**
   * Whether the Vertical Slab is double.
   */
  private final @Nonnull Boolean isDouble;

  public VerticalSlabModelKey(@Nullable Direction side, @Nonnull BlockState stateKey, @Nonnull Boolean isDouble) {
    this.side = side;
    this.state = stateKey;
    this.isDouble = isDouble;
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
    if (side != other.side || !state.equals(other.state) || isDouble != other.isDouble) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash(side, state);
  }
}
